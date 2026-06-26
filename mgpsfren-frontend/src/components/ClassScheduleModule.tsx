import React, { useEffect, useMemo, useState } from 'react';
import * as api from '../api';
import { useAuth } from '../App';

interface ClassScheduleModuleProps {
  schoolId: string;
}

interface ScheduleSlot {
  id?: string;
  className: string;
  academicSession: string;
  weekNumber: number;
  periodName: string;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  scheduleType: 'CORE' | 'ACTIVITY' | 'HOLIDAY';
  subject?: string;
  content?: string;
}

interface SchedulePeriod {
  id?: string;
  className: string;
  academicSession: string;
  periodName: string;
  displayOrder: number;
  startTime: string;
  endTime: string;
}

const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];
const WEEKS = Array.from({ length: 52 }, (_, index) => index + 1);

function defaultSlot(dayOfWeek = 'MONDAY'): Partial<ScheduleSlot> {
  return {
    weekNumber: 1,
    periodName: '',
    dayOfWeek,
    scheduleType: 'CORE',
    subject: ''
  };
}

export function ClassScheduleModule({ schoolId }: ClassScheduleModuleProps) {
  const { user } = useAuth();
  const isAdmin = user?.role === 'SCHOOL_ADMIN' || user?.role === 'SUPER_ADMIN';

  const [academicYears, setAcademicYears] = useState<any[]>([]);
  const [classes, setClasses] = useState<any[]>([]);
  const [subjects, setSubjects] = useState<any[]>([]);
  const [periods, setPeriods] = useState<SchedulePeriod[]>([]);
  const [selectedYearId, setSelectedYearId] = useState('');
  const [selectedClassId, setSelectedClassId] = useState('');
  const [selectedWeek, setSelectedWeek] = useState(1);
  const [schedules, setSchedules] = useState<ScheduleSlot[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [showForm, setShowForm] = useState(false);
  const [editingSlot, setEditingSlot] = useState<ScheduleSlot | null>(null);
  const [formData, setFormData] = useState<Partial<ScheduleSlot>>(defaultSlot());
  const [periodForm, setPeriodForm] = useState({
    periodName: '',
    displayOrder: '',
    startTime: '09:00',
    endTime: '10:00'
  });

  const [showDuplicate, setShowDuplicate] = useState(false);
  const [duplicateData, setDuplicateData] = useState({
    targetClassId: '',
    targetWeekNumber: 1
  });

  const selectedYear = useMemo(
    () => academicYears.find(year => year.yearId === selectedYearId),
    [academicYears, selectedYearId]
  );
  const selectedClass = useMemo(
    () => classes.find(item => item.classId === selectedClassId),
    [classes, selectedClassId]
  );
  const sessionName = selectedYear?.name || '';
  const className = selectedClass?.name || '';

  useEffect(() => {
    let active = true;
    setError(null);

    api.fetchAcademicYears(schoolId)
      .then(years => {
        if (!active) return;
        setAcademicYears(years);
        const activeYear = years.find((year: any) => year.isActive) || years[0];
        setSelectedYearId(activeYear?.yearId || '');
      })
      .catch(() => {
        if (active) setError('Failed to load academic years');
      });

    return () => {
      active = false;
    };
  }, [schoolId]);

  useEffect(() => {
    if (!selectedYearId) {
      setClasses([]);
      setSelectedClassId('');
      return;
    }

    let active = true;
    api.fetchClasses(schoolId, selectedYearId)
      .then(items => {
        if (!active) return;
        setClasses(items);
        setSelectedClassId(current => (items.some((item: any) => item.classId === current) ? current : items[0]?.classId || ''));
      })
      .catch(() => {
        if (active) setError('Failed to load configured classes');
      });

    return () => {
      active = false;
    };
  }, [schoolId, selectedYearId]);

  useEffect(() => {
    if (!selectedClassId) {
      setSubjects([]);
      setPeriods([]);
      setSchedules([]);
      return;
    }

    let active = true;
    api.fetchSubjects(selectedClassId)
      .then(items => {
        if (active) setSubjects(items);
      })
      .catch(() => {
        if (active) setError('Failed to load configured subjects');
      });

    return () => {
      active = false;
    };
  }, [selectedClassId]);

  useEffect(() => {
    if (!className || !sessionName) {
      setPeriods([]);
      return;
    }

    let active = true;
    api.fetchSchedulePeriods(className, sessionName)
      .then(items => {
        if (active) setPeriods(items);
      })
      .catch(() => {
        if (active) setError('Failed to load configured periods');
      });

    return () => {
      active = false;
    };
  }, [className, sessionName]);

  useEffect(() => {
    if (className && sessionName) {
      loadSchedules(className, sessionName, selectedWeek);
    }
  }, [className, sessionName, selectedWeek]);

  useEffect(() => {
    const nextOrder = periods.length + 1;
    setPeriodForm(current => ({
      ...current,
      periodName: current.periodName || `P${nextOrder}`,
      displayOrder: current.displayOrder || String(nextOrder)
    }));
  }, [periods.length]);

  async function loadSchedules(currentClassName = className, currentSession = sessionName, currentWeek = selectedWeek) {
    setLoading(true);
    setError(null);
    try {
      const data = await api.fetchSchedules(currentClassName, currentSession, currentWeek);
      setSchedules(data);
    } catch {
      setError('Failed to load timetable');
    } finally {
      setLoading(false);
    }
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!isAdmin || !className || !sessionName) return;

    const payload = {
      ...formData,
      className,
      academicSession: sessionName,
      weekNumber: selectedWeek,
      subject: formData.scheduleType === 'HOLIDAY' ? '' : formData.subject,
      teacherName: null,
      location: null
    };

    try {
      if (editingSlot?.id) {
        await api.updateSchedule(editingSlot.id, payload);
      } else {
        await api.createSchedule(payload);
      }
      setShowForm(false);
      setEditingSlot(null);
      loadSchedules();
    } catch {
      setError('Failed to save timetable slot');
    }
  }

  async function handleDelete(id: string) {
    if (!isAdmin || !window.confirm('Delete this slot?')) return;
    try {
      await api.deleteSchedule(id);
      loadSchedules();
    } catch {
      setError('Failed to delete slot');
    }
  }

  async function handleDuplicate(e: React.FormEvent) {
    e.preventDefault();
    if (!isAdmin || !className || !sessionName) return;

    const targetClass = classes.find(item => item.classId === duplicateData.targetClassId);
    if (!targetClass) {
      setError('Select a target class');
      return;
    }

    try {
      await api.duplicateSchedule({
        sourceClassName: className,
        sourceSession: sessionName,
        sourceWeekNumber: selectedWeek,
        targetClassName: targetClass.name,
        targetSession: sessionName,
        targetWeekNumber: duplicateData.targetWeekNumber
      });
      setSelectedClassId(targetClass.classId);
      setSelectedWeek(duplicateData.targetWeekNumber);
      setShowDuplicate(false);
      setDuplicateData({ targetClassId: '', targetWeekNumber: duplicateData.targetWeekNumber });
      setError(null);
    } catch {
      setError('Failed to duplicate timetable');
    }
  }

  async function handleSavePeriod(e: React.FormEvent) {
    e.preventDefault();
    if (!isAdmin || !className || !sessionName) return;

    try {
      await api.saveSchedulePeriod({
        className,
        academicSession: sessionName,
        periodName: periodForm.periodName,
        displayOrder: Number(periodForm.displayOrder) || undefined,
        startTime: periodForm.startTime,
        endTime: periodForm.endTime
      });
      setPeriodForm({
        periodName: `P${periods.length + 2}`,
        displayOrder: String(periods.length + 2),
        startTime: periodForm.endTime,
        endTime: periodForm.endTime
      });
      const items = await api.fetchSchedulePeriods(className, sessionName);
      setPeriods(items);
      setError(null);
    } catch {
      setError('Failed to save period configuration');
    }
  }

  async function handleDeletePeriod(id: string) {
    if (!isAdmin || !window.confirm('Delete this period?')) return;
    try {
      await api.deleteSchedulePeriod(id);
      const items = await api.fetchSchedulePeriods(className, sessionName);
      setPeriods(items);
    } catch {
      setError('Failed to delete period');
    }
  }

  function openSlotForm(dayOfWeek: string, slot?: ScheduleSlot) {
    if (!isAdmin) return;
    if (!slot && periods.length === 0) {
      setError('Configure periods for this class before adding timetable slots');
      return;
    }
    setEditingSlot(slot || null);
    setFormData(slot ? { ...slot } : { ...defaultSlot(dayOfWeek), weekNumber: selectedWeek, periodName: periods[0]?.periodName || '' });
    setShowForm(true);
  }

  function renderGrid() {
    if (!className || !sessionName) {
      return <div className="schedule-empty">Configure an academic year and class before viewing timetable slots.</div>;
    }

    return (
      <div className="schedule-grid">
        {DAYS.map(day => {
          const daySlots = schedules
            .filter(slot => slot.dayOfWeek === day)
            .sort((a, b) => a.startTime.localeCompare(b.startTime));

          return (
            <div key={day} className="day-row">
              <div className="day-label">{day}</div>
              <div className="slots-container">
                {daySlots.length === 0 && <span className="empty-day">No slots</span>}
                {daySlots.map(slot => (
                  <div key={slot.id} className={`slot-card type-${slot.scheduleType.toLowerCase()}`}>
                    <div className="slot-time">{slot.startTime} - {slot.endTime}</div>
                    <div className="slot-period">{slot.periodName}</div>
                    <div className="slot-subject">{slot.subject || slot.scheduleType}</div>
                    {slot.content ? <div className="slot-content">{slot.content}</div> : null}
                    {isAdmin && (
                      <div className="slot-actions">
                        <button type="button" onClick={() => openSlotForm(day, slot)}>Edit</button>
                        <button type="button" onClick={() => slot.id && handleDelete(slot.id)}>Delete</button>
                      </div>
                    )}
                  </div>
                ))}
                {isAdmin && (
                  <button type="button" className="add-slot-btn" onClick={() => openSlotForm(day)}>
                    Add Slot
                  </button>
                )}
              </div>
            </div>
          );
        })}
      </div>
    );
  }

  return (
    <div className="module-container">
      <div className="module-header">
        <div>
          <p className="section-label">Timetable</p>
          <h2>Class Schedule Management</h2>
        </div>
        <div className="header-actions schedule-filters">
          <select value={selectedYearId} onChange={e => setSelectedYearId(e.target.value)}>
            <option value="">Select FY</option>
            {academicYears.map(year => <option key={year.yearId} value={year.yearId}>{year.name}</option>)}
          </select>
          <select value={selectedClassId} onChange={e => setSelectedClassId(e.target.value)}>
            <option value="">Select Class</option>
            {classes.map(item => <option key={item.classId} value={item.classId}>{item.name}</option>)}
          </select>
          <select value={selectedWeek} onChange={e => setSelectedWeek(Number(e.target.value))}>
            {WEEKS.map(week => <option key={week} value={week}>Week {week}</option>)}
          </select>
          {isAdmin && (
            <button
              type="button"
              className="secondary"
              onClick={() => {
                setDuplicateData({ targetClassId: '', targetWeekNumber: selectedWeek });
                setShowDuplicate(true);
              }}
              disabled={!className || !sessionName}
            >
              Duplicate Week
            </button>
          )}
        </div>
      </div>

      <div className="schedule-toolbar">
        <span className="badge">{sessionName || 'No FY selected'}</span>
        <span className="badge">{className || 'No class selected'}</span>
        <span className="badge">Week {selectedWeek}</span>
        {!isAdmin && <span className="hint">View only</span>}
      </div>

      {error && <div className="alert alert-error">{error}</div>}
      {isAdmin && className && sessionName && (
        <div className="period-setup">
          <div className="period-header">
            <div>
              <p className="section-label">Period Setup</p>
              <h3>Configure periods for {className}</h3>
            </div>
          </div>
          <form className="period-form" onSubmit={handleSavePeriod}>
            <label>
              Period
              <input
                type="text"
                value={periodForm.periodName}
                onChange={e => setPeriodForm({ ...periodForm, periodName: e.target.value })}
                placeholder="P1"
                required
              />
            </label>
            <label>
              Order
              <input
                type="number"
                min="1"
                value={periodForm.displayOrder}
                onChange={e => setPeriodForm({ ...periodForm, displayOrder: e.target.value })}
                required
              />
            </label>
            <label>
              Start
              <input
                type="time"
                value={periodForm.startTime}
                onChange={e => setPeriodForm({ ...periodForm, startTime: e.target.value })}
                required
              />
            </label>
            <label>
              End
              <input
                type="time"
                value={periodForm.endTime}
                onChange={e => setPeriodForm({ ...periodForm, endTime: e.target.value })}
                required
              />
            </label>
            <button type="submit" className="primary">Save Period</button>
          </form>
          <div className="period-list">
            {periods.length === 0 ? (
              <span className="hint">No periods configured yet.</span>
            ) : (
              periods.map(period => (
                <div key={period.id || period.periodName} className="period-chip">
                  <strong>{period.periodName}</strong>
                  <span>{period.startTime} - {period.endTime}</span>
                  {period.id && <button type="button" onClick={() => handleDeletePeriod(period.id!)}>Delete</button>}
                </div>
              ))
            )}
          </div>
        </div>
      )}
      {loading ? <p>Loading...</p> : renderGrid()}

      {showForm && isAdmin && (
        <div className="schedule-modal-overlay">
          <div className="schedule-modal">
            <h3>{editingSlot ? 'Edit Slot' : 'Add Slot'}</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Day</label>
                <select value={formData.dayOfWeek} onChange={e => setFormData({ ...formData, dayOfWeek: e.target.value })}>
                  {DAYS.map(day => <option key={day} value={day}>{day}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label>Period</label>
                <select value={formData.periodName || ''} onChange={e => setFormData({ ...formData, periodName: e.target.value })} required>
                  <option value="">Select Period</option>
                  {periods.map(period => (
                    <option key={period.id || period.periodName} value={period.periodName}>
                      {period.periodName} ({period.startTime} - {period.endTime})
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>Type</label>
                <select value={formData.scheduleType} onChange={e => setFormData({ ...formData, scheduleType: e.target.value as ScheduleSlot['scheduleType'], subject: '' })}>
                  <option value="CORE">Core Subject</option>
                  <option value="ACTIVITY">Activity</option>
                  <option value="HOLIDAY">Holiday</option>
                </select>
              </div>
              {formData.scheduleType === 'CORE' && (
                <div className="form-group">
                  <label>Subject</label>
                  <select value={formData.subject || ''} onChange={e => setFormData({ ...formData, subject: e.target.value })} required>
                    <option value="">Select Subject</option>
                    {subjects.map(subject => <option key={subject.id} value={subject.name}>{subject.name}</option>)}
                  </select>
                </div>
              )}
              {formData.scheduleType === 'ACTIVITY' && (
                <div className="form-group">
                  <label>Activity Title</label>
                  <input type="text" value={formData.subject || ''} onChange={e => setFormData({ ...formData, subject: e.target.value })} required />
                </div>
              )}
              <div className="form-group">
                <label>Content / Description</label>
                <textarea value={formData.content || ''} onChange={e => setFormData({ ...formData, content: e.target.value })} />
              </div>
              <div className="modal-footer">
                <button type="button" className="secondary" onClick={() => setShowForm(false)}>Cancel</button>
                <button type="submit" className="primary">Save</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showDuplicate && isAdmin && (
        <div className="schedule-modal-overlay">
          <div className="schedule-modal">
            <h3>Duplicate Week</h3>
            <p className="hint">Copy {className} {sessionName} Week {selectedWeek} to another class or week.</p>
            <form onSubmit={handleDuplicate}>
              <div className="form-group">
                <label>Target Class</label>
                <select value={duplicateData.targetClassId} onChange={e => setDuplicateData({ ...duplicateData, targetClassId: e.target.value })} required>
                  <option value="">Select Class</option>
                  {classes.map(item => <option key={item.classId} value={item.classId}>{item.name}</option>)}
                </select>
              </div>
              <div className="form-group">
                <label>Target Week</label>
                <select value={duplicateData.targetWeekNumber} onChange={e => setDuplicateData({ ...duplicateData, targetWeekNumber: Number(e.target.value) })}>
                  {WEEKS.map(week => <option key={week} value={week}>Week {week}</option>)}
                </select>
              </div>
              <div className="modal-footer">
                <button type="button" className="secondary" onClick={() => setShowDuplicate(false)}>Cancel</button>
                <button type="submit" className="primary">Duplicate</button>
              </div>
            </form>
          </div>
        </div>
      )}

      <style>{`
        .schedule-filters { display: flex; flex-wrap: wrap; gap: 10px; align-items: center; }
        .schedule-filters select { min-width: 150px; }
        .schedule-toolbar { display: flex; flex-wrap: wrap; gap: 8px; align-items: center; margin: 14px 0; }
        .period-setup { border: 1px solid rgba(148, 163, 184, 0.28); border-radius: 8px; padding: 14px; margin-bottom: 14px; background: rgba(15, 23, 42, 0.18); }
        .period-header h3 { margin: 0 0 12px; color: var(--text); }
        .period-form { display: grid; grid-template-columns: minmax(100px, 1fr) 90px 130px 130px auto; gap: 10px; align-items: end; }
        .period-form label { color: var(--muted); font-size: 0.86rem; font-weight: 700; }
        .period-form input { width: 100%; margin-top: 5px; padding: 9px; border-radius: 6px; border: 1px solid rgba(148, 163, 184, 0.45); background: rgba(255, 255, 255, 0.95); color: #0f172a; }
        .period-list { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 12px; }
        .period-chip { display: inline-flex; align-items: center; gap: 8px; padding: 7px 9px; border-radius: 8px; background: rgba(255, 255, 255, 0.08); border: 1px solid rgba(148, 163, 184, 0.25); color: var(--text); }
        .period-chip span { color: var(--muted); }
        .period-chip button { border: 1px solid rgba(248, 113, 113, 0.5); background: rgba(248, 113, 113, 0.12); color: #fecaca; border-radius: 6px; padding: 4px 7px; cursor: pointer; }
        .schedule-grid { border: 1px solid rgba(148, 163, 184, 0.28); border-radius: 8px; overflow: hidden; background: rgba(15, 23, 42, 0.18); }
        .day-row { display: grid; grid-template-columns: 126px 1fr; border-bottom: 1px solid rgba(148, 163, 184, 0.2); min-height: 104px; }
        .day-row:last-child { border-bottom: 0; }
        .day-label { padding: 15px; font-weight: 700; color: var(--text); background: rgba(15, 23, 42, 0.36); border-right: 1px solid rgba(148, 163, 184, 0.2); }
        .slots-container { display: flex; flex-wrap: wrap; gap: 10px; align-items: flex-start; padding: 12px; min-width: 0; }
        .slot-card { width: 210px; min-height: 96px; padding: 10px; border-radius: 8px; color: #102033; background: #f8fafc; border-left: 4px solid #64748b; box-shadow: 0 10px 24px rgba(15, 23, 42, 0.18); }
        .type-core { border-left-color: #2563eb; background: #eff6ff; }
        .type-activity { border-left-color: #16a34a; background: #ecfdf5; }
        .type-holiday { border-left-color: #ea580c; background: #fff7ed; }
        .slot-time { font-weight: 800; margin-bottom: 5px; }
        .slot-period { display: inline-flex; margin-bottom: 6px; padding: 2px 7px; border-radius: 999px; background: rgba(15, 23, 42, 0.1); color: #0f172a; font-size: 0.78rem; font-weight: 800; }
        .slot-subject { font-weight: 700; color: #111827; }
        .slot-content, .slot-meta { margin-top: 5px; color: #475569; font-size: 0.86rem; }
        .slot-actions { margin-top: 10px; display: flex; gap: 6px; }
        .slot-actions button { border: 1px solid #cbd5e1; background: #ffffff; color: #0f172a; border-radius: 6px; padding: 5px 8px; cursor: pointer; }
        .add-slot-btn { min-width: 92px; height: 38px; border-radius: 8px; border: 1px dashed rgba(148, 163, 184, 0.8); color: var(--text); background: rgba(255, 255, 255, 0.08); cursor: pointer; }
        .empty-day, .schedule-empty { color: var(--muted); padding: 8px 0; }
        .schedule-modal-overlay { position: fixed; inset: 0; background: rgba(2, 6, 23, 0.7); display: flex; align-items: center; justify-content: center; padding: 18px; z-index: 1000; }
        .schedule-modal { width: min(520px, 100%); max-height: 90vh; overflow: auto; background: #111827; color: #f8fafc; border: 1px solid rgba(148, 163, 184, 0.35); border-radius: 8px; padding: 20px; box-shadow: 0 24px 70px rgba(0, 0, 0, 0.42); }
        .schedule-modal h3 { margin-top: 0; color: #ffffff; }
        .schedule-modal .form-group { margin-bottom: 14px; }
        .schedule-modal .form-group label { display: block; margin-bottom: 6px; color: #e5e7eb; font-weight: 700; }
        .schedule-modal .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
        .schedule-modal input, .schedule-modal select, .schedule-modal textarea { width: 100%; padding: 10px; border: 1px solid #475569; border-radius: 6px; background: #ffffff; color: #0f172a; }
        .schedule-modal textarea { min-height: 82px; resize: vertical; }
        .modal-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 18px; }
        @media (max-width: 720px) {
          .period-form { grid-template-columns: 1fr; }
          .day-row { grid-template-columns: 1fr; }
          .day-label { border-right: 0; border-bottom: 1px solid rgba(148, 163, 184, 0.2); }
          .slot-card { width: 100%; }
          .schedule-modal .form-row { grid-template-columns: 1fr; }
        }
      `}</style>
    </div>
  );
}
