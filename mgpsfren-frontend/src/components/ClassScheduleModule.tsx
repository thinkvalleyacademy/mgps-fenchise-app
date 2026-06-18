import React, { useState, useEffect } from 'react';
import * as api from '../api';
import { useAuth } from '../App';

interface ScheduleSlot {
  id?: string;
  className: string;
  academicSession: string;
  dayOfWeek: string;
  startTime: string;
  endTime: string;
  scheduleType: 'CORE' | 'ACTIVITY' | 'HOLIDAY';
  subject?: string;
  content?: string;
  location?: string;
  teacherName?: string;
}

const DAYS = ['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY'];

export function ClassScheduleModule() {
  const { user } = useAuth();
  const isAdmin = user?.role === 'SCHOOL_ADMIN' || user?.role === 'SUPER_ADMIN';

  const [className, setClassName] = useState('10A');
  const [session, setSession] = useState('2024-25');
  const [schedules, setSchedules] = useState<ScheduleSlot[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Form State
  const [showForm, setShowForm] = useState(false);
  const [editingSlot, setEditingSlot] = useState<ScheduleSlot | null>(null);
  const [formData, setFormData] = useState<Partial<ScheduleSlot>>({
    dayOfWeek: 'MONDAY',
    startTime: '09:00',
    endTime: '10:00',
    scheduleType: 'CORE'
  });

  // Duplicate State
  const [showDuplicate, setShowDuplicate] = useState(false);
  const [duplicateData, setDuplicateData] = useState({
    targetClassName: '',
    targetSession: session
  });

  useEffect(() => {
    loadSchedules();
  }, [className, session]);

  async function loadSchedules() {
    setLoading(true);
    setError(null);
    try {
      const data = await api.fetchSchedules(className, session);
      setSchedules(data);
    } catch (err: any) {
      setError('Failed to load schedules');
    } finally {
      setLoading(false);
    }
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      if (editingSlot?.id) {
        await api.updateSchedule(editingSlot.id, { ...formData, className, academicSession: session });
      } else {
        await api.createSchedule({ ...formData, className, academicSession: session });
      }
      setShowForm(false);
      setEditingSlot(null);
      loadSchedules();
    } catch (err: any) {
      setError('Failed to save schedule');
    }
  }

  async function handleDelete(id: string) {
    if (!window.confirm('Delete this slot?')) return;
    try {
      await api.deleteSchedule(id);
      loadSchedules();
    } catch (err: any) {
      setError('Failed to delete slot');
    }
  }

  async function handleDuplicate(e: React.FormEvent) {
    e.preventDefault();
    try {
      await api.duplicateSchedule({
        sourceClassName: className,
        sourceSession: session,
        targetClassName: duplicateData.targetClassName,
        targetSession: duplicateData.targetSession
      });
      setShowDuplicate(false);
      alert('Schedule duplicated successfully');
    } catch (err: any) {
      setError('Failed to duplicate schedule');
    }
  }

  function renderGrid() {
    return (
      <div className="schedule-grid">
        <div className="schedule-header">
          <div className="time-col">Time</div>
          {DAYS.map(day => <div key={day} className="day-col">{day}</div>)}
        </div>
        <div className="schedule-body">
          {/* Simple list view for now, could be enhanced to a proper time-grid */}
          {DAYS.map(day => (
            <div key={day} className="day-row">
              <div className="day-label">{day}</div>
              <div className="slots-container">
                {schedules
                  .filter(s => s.dayOfWeek === day)
                  .sort((a, b) => a.startTime.localeCompare(b.startTime))
                  .map(slot => (
                    <div key={slot.id} className={`slot-card type-${slot.scheduleType.toLowerCase()}`}>
                      <div className="slot-time">{slot.startTime} - {slot.endTime}</div>
                      <div className="slot-subject">{slot.subject || slot.scheduleType}</div>
                      <div className="slot-content">{slot.content}</div>
                      {isAdmin && (
                        <div className="slot-actions">
                          <button onClick={() => { setEditingSlot(slot); setFormData(slot); setShowForm(true); }}>Edit</button>
                          <button onClick={() => slot.id && handleDelete(slot.id)}>Delete</button>
                        </div>
                      )}
                    </div>
                  ))}
                {isAdmin && (
                  <button className="add-slot-btn" onClick={() => { 
                    setFormData({ dayOfWeek: day, startTime: '09:00', endTime: '10:00', scheduleType: 'CORE' });
                    setEditingSlot(null);
                    setShowForm(true);
                  }}>+</button>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="module-container">
      <div className="module-header">
        <h2>Class Schedule Management</h2>
        <div className="header-actions">
          <select value={className} onChange={e => setClassName(e.target.value)}>
            <option value="10A">Class 10A</option>
            <option value="10B">Class 10B</option>
            <option value="9A">Class 9A</option>
          </select>
          <select value={session} onChange={e => setSession(e.target.value)}>
            <option value="2024-25">2024-25</option>
            <option value="2023-24">2023-24</option>
          </select>
          {isAdmin && (
            <button className="btn-secondary" onClick={() => setShowDuplicate(true)}>Duplicate Schedule</button>
          )}
        </div>
      </div>

      {error && <div className="alert alert-error">{error}</div>}
      {loading ? <p>Loading...</p> : renderGrid()}

      {showForm && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>{editingSlot ? 'Edit Slot' : 'Add Slot'}</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Day</label>
                <select value={formData.dayOfWeek} onChange={e => setFormData({ ...formData, dayOfWeek: e.target.value })}>
                  {DAYS.map(d => <option key={d} value={d}>{d}</option>)}
                </select>
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Start Time</label>
                  <input type="time" value={formData.startTime} onChange={e => setFormData({ ...formData, startTime: e.target.value })} required />
                </div>
                <div className="form-group">
                  <label>End Time</label>
                  <input type="time" value={formData.endTime} onChange={e => setFormData({ ...formData, endTime: e.target.value })} required />
                </div>
              </div>
              <div className="form-group">
                <label>Type</label>
                <select value={formData.scheduleType} onChange={e => setFormData({ ...formData, scheduleType: e.target.value as any })}>
                  <option value="CORE">Core Subject</option>
                  <option value="ACTIVITY">Activity</option>
                  <option value="HOLIDAY">Holiday</option>
                </select>
              </div>
              {formData.scheduleType !== 'HOLIDAY' && (
                <div className="form-group">
                  <label>Subject / Title</label>
                  <input type="text" value={formData.subject || ''} onChange={e => setFormData({ ...formData, subject: e.target.value })} />
                </div>
              )}
              <div className="form-group">
                <label>Content / Description</label>
                <textarea value={formData.content || ''} onChange={e => setFormData({ ...formData, content: e.target.value })} />
              </div>
              <div className="modal-footer">
                <button type="button" onClick={() => setShowForm(false)}>Cancel</button>
                <button type="submit" className="btn-primary">Save</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showDuplicate && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Duplicate Schedule</h3>
            <p>Copy schedule from {className} ({session}) to:</p>
            <form onSubmit={handleDuplicate}>
              <div className="form-group">
                <label>Target Class</label>
                <input type="text" value={duplicateData.targetClassName} onChange={e => setDuplicateData({ ...duplicateData, targetClassName: e.target.value })} placeholder="e.g. 10B" required />
              </div>
              <div className="form-group">
                <label>Target Session</label>
                <input type="text" value={duplicateData.targetSession} onChange={e => setDuplicateData({ ...duplicateData, targetSession: e.target.value })} required />
              </div>
              <div className="modal-footer">
                <button type="button" onClick={() => setShowDuplicate(false)}>Cancel</button>
                <button type="submit" className="btn-primary">Duplicate</button>
              </div>
            </form>
          </div>
        </div>
      )}

      <style>{`
        .schedule-grid { margin-top: 20px; border: 1px solid #ddd; border-radius: 8px; overflow: hidden; }
        .day-row { display: flex; border-bottom: 1px solid #eee; min-height: 100px; }
        .day-label { width: 120px; padding: 15px; background: #f9f9f9; font-weight: bold; border-right: 1px solid #eee; }
        .slots-container { flex: 1; display: flex; flex-wrap: wrap; gap: 10px; padding: 10px; }
        .slot-card { width: 200px; padding: 10px; border-radius: 6px; font-size: 0.9em; position: relative; }
        .type-core { background: #e3f2fd; border-left: 4px solid #2196f3; }
        .type-activity { background: #e8f5e9; border-left: 4px solid #4caf50; }
        .type-holiday { background: #fff3e0; border-left: 4px solid #ff9800; }
        .slot-time { font-weight: bold; margin-bottom: 5px; }
        .slot-actions { margin-top: 10px; display: none; gap: 5px; }
        .slot-card:hover .slot-actions { display: flex; }
        .add-slot-btn { width: 40px; height: 40px; border-radius: 50%; border: 1px dashed #ccc; background: none; cursor: pointer; }
        .modal-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
        .modal-content { background: white; padding: 20px; border-radius: 8px; width: 400px; max-width: 90%; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; }
        .form-group input, .form-group select, .form-group textarea { width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px; }
        .modal-footer { display: flex; justify-content: flex-end; gap: 10px; margin-top: 20px; }
      `}</style>
    </div>
  );
}
