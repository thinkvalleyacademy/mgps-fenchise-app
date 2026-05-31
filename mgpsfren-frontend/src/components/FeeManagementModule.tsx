import { useEffect, useState } from 'react';
import { fetchFeeCategories, createFeeCategory, fetchFeeStructures, createFeeStructure, fetchStudents, fetchStudentFees, processPayment, fetchAcademicYears, applyFeeDiscount, fetchSchoolFeeReport, fetchClassFeeReport, fetchStudentFeeReport, fetchClasses, assignFeeToStudent, fetchRecentPayments } from '../api';
import FeeCollectionModal from './FeeCollectionModal';

interface FeeManagementModuleProps {
  schoolId: string;
}

export default function FeeManagementModule({ schoolId }: FeeManagementModuleProps) {
  const [activeTab, setActiveTab] = useState<'dashboard' | 'categories' | 'structures' | 'collection' | 'reports'>('dashboard');
  const [categories, setCategories] = useState<any[]>([]);
  const [structures, setStructures] = useState<any[]>([]);
  const [academicYears, setAcademicYears] = useState<any[]>([]);
  const [classes, setClasses] = useState<any[]>([]);
  const [selectedYearId, setSelectedYearId] = useState<string>('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Form states
  const [categoryForm, setCategoryForm] = useState({ name: '', description: '' });
  const [structureForm, setStructureForm] = useState({ feeCategoryId: '', amount: '', dueDate: '', classId: '', isDefault: true, recurrenceType: 'ONE_TIME' });

  // Reporting states
  const [schoolReport, setSchoolReport] = useState<any>(null);
  const [classReports, setClassReports] = useState<any[]>([]);
  const [selectedClassId, setSelectedClassId] = useState<string>('');
  const [studentReports, setStudentReports] = useState<any[]>([]);
  const [recentPayments, setRecentPayments] = useState<any[]>([]);

  // Collection states
  const [students, setStudents] = useState<any[]>([]);
  const [selectedStudent, setSelectedStudent] = useState<any>(null);
  const [studentFees, setStudentFees] = useState<any[]>([]);
  const [showCollectionModal, setShowCollectionModal] = useState(false);
  const [collectingFee, setCollectingFee] = useState<any>(null);

  useEffect(() => {
    loadBaseData();
    fetchClasses(schoolId).then(setClasses);
  }, [schoolId]);

  async function loadBaseData() {
    setIsLoading(true);
    try {
      const [cats, years] = await Promise.all([
        fetchFeeCategories(schoolId),
        fetchAcademicYears(schoolId)
      ]);
      setCategories(cats);
      setAcademicYears(years);
      const active = years.find(y => y.isActive) || years[0];
      if (active) {
        setSelectedYearId(active.yearId);
      }
    } catch (err) {
      setError('Failed to load fee data');
    } finally {
      setIsLoading(false);
    }
  }

  useEffect(() => {
    if (selectedYearId && activeTab === 'structures') {
      loadStructures();
    }
    if (selectedYearId && activeTab === 'dashboard') {
        fetchSchoolFeeReport(schoolId, selectedYearId).then(setSchoolReport);
        fetchRecentPayments(schoolId).then(setRecentPayments);
    }
    if (selectedYearId && activeTab === 'reports') {
        fetchClassFeeReport(schoolId, selectedYearId).then(setClassReports);
    }
  }, [selectedYearId, activeTab, schoolId]);

  useEffect(() => {
      if (selectedClassId && activeTab === 'reports') {
          fetchStudentFeeReport(selectedClassId).then(setStudentReports);
      }
  }, [selectedClassId, activeTab]);

  async function loadStructures() {
    try {
      const data = await fetchFeeStructures(schoolId, selectedYearId);
      setStructures(data);
    } catch (err) {
      setError('Failed to load fee structures');
    }
  }

  async function handleCreateCategory(e: React.FormEvent) {
    e.preventDefault();
    try {
      await createFeeCategory({ ...categoryForm, schoolId });
      setCategoryForm({ name: '', description: '' });
      const cats = await fetchFeeCategories(schoolId);
      setCategories(cats);
    } catch (err) {
      setError('Failed to create category');
    }
  }

  async function handleCreateStructure(e: React.FormEvent) {
    e.preventDefault();
    try {
      await createFeeStructure({
        ...structureForm,
        schoolId,
        academicYearId: selectedYearId,
        amount: parseFloat(structureForm.amount),
        classId: structureForm.classId === '' ? null : structureForm.classId
      });
      setStructureForm({ feeCategoryId: '', amount: '', dueDate: '', classId: '', isDefault: true, recurrenceType: 'ONE_TIME' });
      loadStructures();
    } catch (err) {
      setError('Failed to create fee structure');
    }
  }

  const [showDiscountForm, setShowDiscountForm] = useState<string | null>(null);
  const [discountForm, setDiscountForm] = useState({ amount: '', reason: '' });

  const [availableStructures, setAvailableFeesToAssign] = useState<any[]>([]);
  const [showAssignForm, setShowAssignForm] = useState(false);
  const [assigningStructureId, setAssigningStructureId] = useState('');

  useEffect(() => {
    if (activeTab === 'collection') {
      loadStudents();
    }
  }, [activeTab]);

  useEffect(() => {
      if (selectedStudent && selectedYearId) {
          fetchFeeStructures(schoolId, selectedYearId).then(all => {
              // Filter out ones already assigned
              const assignedIds = studentFees.map(sf => sf.feeStructureId);
              const unassigned = all.filter((s: any) => !assignedIds.includes(s.id));
              setAvailableFeesToAssign(unassigned);
          });
      }
  }, [selectedStudent, studentFees, selectedYearId, schoolId]);

  async function loadStudents() {
    try {
      const data = await fetchStudents(schoolId);
      setStudents(data);
    } catch (err) {
      setError('Failed to load students');
    }
  }

  async function handleSelectStudent(student: any) {
    setSelectedStudent(student);
    setShowAssignForm(false);
    try {
      const fees = await fetchStudentFees(student.studentId || student.id);
      setStudentFees(fees);
    } catch (err) {
      setError('Failed to load student fees');
    }
  }

  async function handleAssignFee(e: React.FormEvent) {
      e.preventDefault();
      if (!selectedStudent || !assigningStructureId) return;
      try {
          await assignFeeToStudent(selectedStudent.studentId || selectedStudent.id, assigningStructureId);
          setAssigningStructureId('');
          setShowAssignForm(false);
          handleSelectStudent(selectedStudent);
      } catch (err) {
          alert('Failed to assign fee');
      }
  }

  async function handleApplyDiscount(e: React.FormEvent) {
      e.preventDefault();
      if (!showDiscountForm) return;
      try {
          await applyFeeDiscount(showDiscountForm, parseFloat(discountForm.amount), discountForm.reason);
          setShowDiscountForm(null);
          setDiscountForm({ amount: '', reason: '' });
          handleSelectStudent(selectedStudent); // Refresh
      } catch (err) {
          alert('Failed to apply discount');
      }
  }

  function handleCollectClick(fee: any) {
      setCollectingFee(fee);
      setShowCollectionModal(true);
  }

  return (
    <section className="module-panel">
      <div className="module-header">
        <div>
          <p className="section-label">Finance & Fees</p>
          <h2>{activeTab === 'dashboard' ? 'Fee Dashboard' : activeTab === 'categories' ? 'Fee Categories' : activeTab === 'structures' ? 'Fee Structure' : activeTab === 'collection' ? 'Fee Collection' : 'Fee Reports'}</h2>
        </div>
        <div style={{ display: 'flex', gap: 12, alignItems: 'center' }}>
            <select className="secondary small" value={selectedYearId} onChange={e => setSelectedYearId(e.target.value)}>
                {academicYears.map(y => <option key={y.yearId} value={y.yearId}>{y.name}</option>)}
            </select>
            <span className="badge">Active Session</span>
        </div>
      </div>

      <div className="tabs" style={{ marginBottom: 24 }}>
        <button className={activeTab === 'dashboard' ? 'active' : ''} onClick={() => setActiveTab('dashboard')}>Dashboard</button>
        <button className={activeTab === 'categories' ? 'active' : ''} onClick={() => setActiveTab('categories')}>Categories</button>
        <button className={activeTab === 'structures' ? 'active' : ''} onClick={() => setActiveTab('structures')}>Structure</button>
        <button className={activeTab === 'collection' ? 'active' : ''} onClick={() => setActiveTab('collection')}>Collection</button>
        <button className={activeTab === 'reports' ? 'active' : ''} onClick={() => setActiveTab('reports')}>Reports</button>
      </div>

      {error && <p className="error">{error}</p>}

      {activeTab === 'dashboard' && schoolReport && (
        <>
            <div className="module-card-grid">
            <article className="module-card">
                <h4>Total Receivables</h4>
                <p className="stat-value">₹{schoolReport.overallExpected.toLocaleString()}</p>
                <p className="hint">Expected fees for selected session</p>
            </article>
            <article className="module-card">
                <h4>Total Collected</h4>
                <p className="stat-value" style={{ color: 'var(--accent)' }}>₹{schoolReport.overallCollected.toLocaleString()}</p>
                <p className="hint">Successfully processed payments</p>
            </article>
            <article className="module-card">
                <h4>Discounts Given</h4>
                <p className="stat-value" style={{ color: 'var(--gold)' }}>₹{schoolReport.overallDiscounted.toLocaleString()}</p>
                <p className="hint">Total fee waivers applied</p>
            </article>
            <article className="module-card">
                <h4>Outstanding</h4>
                <p className="stat-value" style={{ color: 'var(--danger)' }}>₹{schoolReport.overallOutstanding.toLocaleString()}</p>
                <p className="hint">Pending dues from {schoolReport.activeStudentsCount} students</p>
            </article>
            </div>

            <div className="card" style={{ marginTop: 24 }}>
                <h3>Recent Collections</h3>
                <div className="table-wrap">
                    <table className="module-table">
                        <thead>
                            <tr><th>Date</th><th>Receipt</th><th>Amount</th><th>Mode</th><th>Remarks</th></tr>
                        </thead>
                        <tbody>
                            {recentPayments.map((p, idx) => (
                                <tr key={p.receiptNumber || idx}>
                                    <td>{new Date(p.paymentDate).toLocaleDateString()}</td>
                                    <td><code>{p.receiptNumber}</code></td>
                                    <td><strong>₹{p.amountPaid.toLocaleString()}</strong></td>
                                    <td><span className="badge">{p.paymentMode}</span></td>
                                    <td className="hint">{p.remarks}</td>
                                </tr>
                            ))}
                            {recentPayments.length === 0 && <tr><td colSpan={5} style={{ textAlign: 'center' }}>No recent payments found.</td></tr>}
                        </tbody>
                    </table>
                </div>
            </div>
        </>
      )}

      {activeTab === 'categories' && (
        <div className="module-shell">
          <form className="card" onSubmit={handleCreateCategory}>
            <h3>Add New Category</h3>
            <div className="form-grid">
              <label>
                Category Name
                <input value={categoryForm.name} onChange={e => setCategoryForm({ ...categoryForm, name: e.target.value })} placeholder="e.g. Tuition Fee" required />
              </label>
              <label className="full">
                Description
                <textarea value={categoryForm.description} onChange={e => setCategoryForm({ ...categoryForm, description: e.target.value })} placeholder="Details about this fee head" />
              </label>
            </div>
            <div className="actions">
              <button type="submit" className="primary">Create Category</button>
            </div>
          </form>

          <div className="table-wrap">
            <table className="module-table">
              <thead>
                <tr>
                  <th>Category Name</th>
                  <th>Description</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {categories.map(cat => (
                  <tr key={cat.id}>
                    <td>{cat.name}</td>
                    <td>{cat.description || '-'}</td>
                    <td><span className="badge">{cat.active ? 'Active' : 'Inactive'}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {activeTab === 'structures' && (
        <div className="module-shell">
          <form className="card" onSubmit={handleCreateStructure}>
            <h3>Define Fee for {academicYears.find(y => y.yearId === selectedYearId)?.name}</h3>
            <div className="form-grid">
              <label>
                Category
                <select value={structureForm.feeCategoryId} onChange={e => setStructureForm({ ...structureForm, feeCategoryId: e.target.value })} required>
                  <option value="">Select Category</option>
                  {categories.map(cat => <option key={cat.id} value={cat.id}>{cat.name}</option>)}
                </select>
              </label>
              <label>
                Amount (₹)
                <input type="number" value={structureForm.amount} onChange={e => setStructureForm({ ...structureForm, amount: e.target.value })} placeholder="0.00" required />
              </label>
              <label>
                Recurrence
                <select value={structureForm.recurrenceType} onChange={e => setStructureForm({ ...structureForm, recurrenceType: e.target.value })} required>
                  <option value="ONE_TIME">One-time (Admission/Annual)</option>
                  <option value="MONTHLY">Monthly Recurring</option>
                </select>
              </label>
              <label>
                Due Date
                <input type="date" value={structureForm.dueDate} onChange={e => setStructureForm({ ...structureForm, dueDate: e.target.value })} required />
              </label>
              <label>
                Target Class (Optional)
                <select value={structureForm.classId} onChange={e => setStructureForm({ ...structureForm, classId: e.target.value })}>
                  <option value="">All Classes</option>
                  {classes.map(c => <option key={c.classId} value={c.classId}>{c.name}</option>)}
                </select>
              </label>
              <label style={{ display: 'flex', alignItems: 'center', gap: 8, cursor: 'pointer' }}>
                <input type="checkbox" checked={structureForm.isDefault} onChange={e => setStructureForm({ ...structureForm, isDefault: e.target.checked })} />
                <span>Mark as Default (Auto-assign on admission)</span>
              </label>
            </div>
            <div className="actions">
              <button type="submit" className="primary">Save Structure</button>
            </div>
          </form>

          <div className="table-wrap">
            <table className="module-table">
              <thead>
                <tr>
                  <th>Fee Category</th>
                  <th>Amount</th>
                  <th>Recurrence</th>
                  <th>Due Date</th>
                  <th>Default</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {structures.map(s => (
                  <tr key={s.id}>
                    <td>{s.feeCategoryName}</td>
                    <td>₹{s.amount.toLocaleString()}</td>
                    <td><span className="badge">{s.recurrenceType}</span></td>
                    <td>{s.dueDate}</td>
                    <td>{s.isDefault ? '✅' : '❌'}</td>
                    <td><span className="badge">{s.active ? 'Active' : 'Inactive'}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {activeTab === 'collection' && (
        <div className="module-shell" style={{ display: 'grid', gridTemplateColumns: '300px 1fr', gap: 20 }}>
          <div className="card">
            <h3>Students</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8, marginTop: 12, maxHeight: '600px', overflowY: 'auto' }}>
              {students.map(s => (
                <button
                  key={s.studentId || s.id}
                  className={`secondary ${selectedStudent?.studentId === s.studentId || selectedStudent?.id === s.id ? 'active' : ''}`}
                  style={{ textAlign: 'left', padding: '12px' }}
                  onClick={() => handleSelectStudent(s)}
                >
                  <strong>{s.firstName} {s.lastName}</strong>
                  <br />
                  <span className="hint">{s.admissionNumber}</span>
                </button>
              ))}
              {students.length === 0 && <p className="hint">No students found.</p>}
            </div>
          </div>

          <div className="module-shell">
            {selectedStudent ? (
              <>
                <div className="card">
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
                      <h3>Fees for {selectedStudent.firstName} {selectedStudent.lastName}</h3>
                      <button className="primary small" onClick={() => setShowAssignForm(!showAssignForm)}>{showAssignForm ? 'Cancel' : 'Assign New Fee'}</button>
                  </div>

                  {showAssignForm && (
                      <form onSubmit={handleAssignFee} style={{ marginBottom: 20, padding: 16, background: 'rgba(255,255,255,0.03)', borderRadius: 8 }}>
                          <div style={{ display: 'flex', gap: 12, alignItems: 'flex-end' }}>
                              <label style={{ flex: 1 }}>
                                  Select Fee to Assign
                                  <select value={assigningStructureId} onChange={e => setAssigningStructureId(e.target.value)} required>
                                      <option value="">Select Structure</option>
                                      {availableStructures.map(s => (
                                          <option key={s.id} value={s.id}>{s.feeCategoryName} - ₹{s.amount.toLocaleString()}</option>
                                      ))}
                                  </select>
                              </label>
                              <button type="submit" className="primary" disabled={!assigningStructureId}>Assign</button>
                          </div>
                          {availableStructures.length === 0 && <p className="hint" style={{ marginTop: 8 }}>No additional fee structures available for this year.</p>}
                      </form>
                  )}

                  <div className="table-wrap">
                    <table className="module-table">
                      <thead>
                        <tr>
                          <th>Fee Head</th>
                          <th>Type</th>
                          <th>Rate/Amount</th>
                          <th>Total Due</th>
                          <th>Discount</th>
                          <th>Paid</th>
                          <th>Balance</th>
                          <th>Status</th>
                          <th>Action</th>
                        </tr>
                      </thead>
                      <tbody>
                        {studentFees.map(sf => {
                            const balance = (sf.totalDueTillDate || sf.amountDue) - sf.amountPaid - (sf.discountAmount || 0);
                            return (
                          <tr key={sf.id}>
                            <td>{sf.feeCategoryName}</td>
                            <td><span className="hint">{sf.recurrenceType}</span></td>
                            <td>₹{sf.amountDue.toLocaleString()}</td>
                            <td>₹{(sf.totalDueTillDate || sf.amountDue).toLocaleString()}</td>
                            <td>
                                {sf.discountAmount > 0 ? (
                                    <span title={sf.discountReason} style={{ color: 'var(--gold)' }}>-₹{sf.discountAmount.toLocaleString()}</span>
                                ) : (
                                    <button className="secondary small" onClick={() => setShowDiscountForm(sf.id)}>Add</button>
                                )}
                            </td>
                            <td>₹{sf.amountPaid.toLocaleString()}</td>
                            <td><strong style={{ color: balance > 0 ? 'var(--danger)' : '' }}>₹{balance.toLocaleString()}</strong></td>
                            <td><span className={`status-pill ${sf.status.toLowerCase()}`}>{sf.status}</span></td>
                            <td>
                              {sf.status !== 'PAID' && (
                                <button className="primary small" style={{ padding: '6px 12px' }} onClick={() => handleCollectClick(sf)}>Collect</button>
                              )}
                            </td>
                          </tr>
                        )})}
                        {studentFees.length === 0 && (
                          <tr>
                            <td colSpan={9} style={{ textAlign: 'center' }}>No fees assigned to this student.</td>
                          </tr>
                        )}
                      </tbody>
                    </table>
                  </div>
                </div>

                {showDiscountForm && (
                    <div className="modal-overlay" style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.8)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 }}>
                        <form className="card" style={{ width: '400px' }} onSubmit={handleApplyDiscount}>
                            <h3>Apply Fee Waiver / Discount</h3>
                            <div className="form-grid" style={{ gridTemplateColumns: '1fr' }}>
                                <label>
                                    Discount Amount (₹)
                                    <input type="number" value={discountForm.amount} onChange={e => setDiscountForm({ ...discountForm, amount: e.target.value })} required />
                                </label>
                                <label>
                                    Reason
                                    <input value={discountForm.reason} onChange={e => setDiscountForm({ ...discountForm, reason: e.target.value })} placeholder="e.g. Merit Scholarship" required />
                                </label>
                            </div>
                            <div className="actions" style={{ marginTop: 20 }}>
                                <button type="submit" className="primary">Apply Discount</button>
                                <button type="button" className="secondary" onClick={() => setShowDiscountForm(null)}>Cancel</button>
                            </div>
                        </form>
                    </div>
                )}

                {showCollectionModal && collectingFee && (
                    <FeeCollectionModal
                        student={selectedStudent}
                        fee={collectingFee}
                        schoolId={schoolId}
                        onClose={() => { setShowCollectionModal(false); setCollectingFee(null); }}
                        onSuccess={() => { handleSelectStudent(selectedStudent); fetchRecentPayments(schoolId).then(setRecentPayments); }}
                    />
                )}
              </>
            ) : (
              <div className="empty-state">
                <p>Select a student from the left to manage their fee collection.</p>
              </div>
            )}
          </div>
        </div>
      )}

      {activeTab === 'reports' && (
          <div className="module-shell">
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: 24 }}>
                  <div className="card">
                      <h3>Class-wise Review</h3>
                      <div className="table-wrap">
                          <table className="module-table">
                              <thead>
                                  <tr><th>Class</th><th>Expected</th><th>Collected</th><th>Pending</th></tr>
                              </thead>
                              <tbody>
                                  {classReports.map(cr => (
                                      <tr key={cr.classId} onClick={() => setSelectedClassId(cr.classId)} style={{ cursor: 'pointer', background: selectedClassId === cr.classId ? 'rgba(255,255,255,0.05)' : '' }}>
                                          <td><strong>{cr.className}</strong></td>
                                          <td>₹{cr.totalExpected.toLocaleString()}</td>
                                          <td style={{ color: 'var(--accent)' }}>₹{cr.totalCollected.toLocaleString()}</td>
                                          <td style={{ color: 'var(--danger)' }}>₹{cr.totalOutstanding.toLocaleString()}</td>
                                      </tr>
                                  ))}
                              </tbody>
                          </table>
                      </div>
                  </div>

                  <div className="card">
                      <h3>{selectedClassId ? `Student Details - ${classReports.find(c => c.classId === selectedClassId)?.className}` : 'Select a class to view details'}</h3>
                      {selectedClassId ? (
                          <div className="table-wrap">
                            <table className="module-table">
                               <thead>
                                    <tr><th>Student</th><th>Expected</th><th>Paid</th><th>Discount</th><th>Balance</th><th>Status</th></tr>
                                </thead>
                                <tbody>
                                    {studentReports.map(sr => (
                                        <tr key={sr.studentId}>
                                            <td>
                                                <strong>{sr.studentName}</strong><br/>
                                                <span className="hint">{sr.admissionNumber}</span>
                                            </td>
                                            <td>₹{sr.totalExpected.toLocaleString()}</td>
                                            <td>₹{sr.totalCollected.toLocaleString()}</td>
                                            <td>₹{sr.totalDiscounted.toLocaleString()}</td>
                                            <td><strong>₹{sr.totalOutstanding.toLocaleString()}</strong></td>
                                            <td><span className={`status-pill ${sr.status.toLowerCase()}`}>{sr.status}</span></td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                          </div>
                      ) : (
                          <div className="empty-state">
                              <p>Select a class from the left to drill down into student-wise fee status.</p>
                          </div>
                      )}
                  </div>
              </div>
          </div>
      )}
    </section>
  );
}
