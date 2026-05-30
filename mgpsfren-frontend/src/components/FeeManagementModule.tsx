import { useEffect, useState } from 'react';
import { fetchFeeCategories, createFeeCategory, fetchFeeStructures, createFeeStructure, fetchStudents, fetchStudentFees, processPayment, fetchAcademicYears } from '../api';

interface FeeManagementModuleProps {
  schoolId: string;
}

export default function FeeManagementModule({ schoolId }: FeeManagementModuleProps) {
  const [activeTab, setActiveTab] = useState<'dashboard' | 'categories' | 'structures' | 'collection'>('dashboard');
  const [categories, setCategories] = useState<any[]>([]);
  const [structures, setStructures] = useState<any[]>([]);
  const [academicYears, setAcademicYears] = useState<any[]>([]);
  const [selectedYearId, setSelectedYearId] = useState<string>('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Form states
  const [categoryForm, setCategoryForm] = useState({ name: '', description: '' });
  const [structureForm, setStructureForm] = useState({ feeCategoryId: '', amount: '', dueDate: '', classId: '' });

  useEffect(() => {
    loadBaseData();
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
      if (years.length > 0) {
        setSelectedYearId(years[0].id);
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
  }, [selectedYearId, activeTab]);

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
        amount: parseFloat(structureForm.amount)
      });
      setStructureForm({ feeCategoryId: '', amount: '', dueDate: '', classId: '' });
      loadStructures();
    } catch (err) {
      setError('Failed to create fee structure');
    }
  }

  const [students, setStudents] = useState<any[]>([]);
  const [selectedStudent, setSelectedStudent] = useState<any>(null);
  const [studentFees, setStudentFees] = useState<any[]>([]);

  useEffect(() => {
    if (activeTab === 'collection') {
      loadStudents();
    }
  }, [activeTab]);

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
    try {
      const fees = await fetchStudentFees(student.id);
      setStudentFees(fees);
    } catch (err) {
      setError('Failed to load student fees');
    }
  }

  return (
    <section className="module-panel">
      <div className="module-header">
        <div>
          <p className="section-label">Finance & Fees</p>
          <h2>{activeTab === 'dashboard' ? 'Fee Dashboard' : activeTab === 'categories' ? 'Fee Categories' : activeTab === 'structures' ? 'Fee Structure' : 'Fee Collection'}</h2>
        </div>
        <span className="badge">Active Session</span>
      </div>

      <div className="tabs" style={{ marginBottom: 24 }}>
        <button className={activeTab === 'dashboard' ? 'active' : ''} onClick={() => setActiveTab('dashboard')}>Dashboard</button>
        <button className={activeTab === 'categories' ? 'active' : ''} onClick={() => setActiveTab('categories')}>Categories</button>
        <button className={activeTab === 'structures' ? 'active' : ''} onClick={() => setActiveTab('structures')}>Structure</button>
        <button className={activeTab === 'collection' ? 'active' : ''} onClick={() => setActiveTab('collection')}>Collection</button>
      </div>

      {error && <p className="error">{error}</p>}

      {activeTab === 'dashboard' && (
        <div className="module-card-grid">
          <article className="module-card">
            <h4>Total Receivables</h4>
            <p className="stat-value">₹0</p>
            <p className="hint">Expected fees for current session</p>
          </article>
          <article className="module-card">
            <h4>Total Collected</h4>
            <p className="stat-value" style={{ color: 'var(--accent)' }}>₹0</p>
            <p className="hint">Successfully processed payments</p>
          </article>
          <article className="module-card">
            <h4>Outstanding</h4>
            <p className="stat-value" style={{ color: 'var(--danger)' }}>₹0</p>
            <p className="hint">Pending dues from students</p>
          </article>
        </div>
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
          <div className="card" style={{ marginBottom: 20 }}>
            <label>
              Select Academic Year
              <select value={selectedYearId} onChange={e => setSelectedYearId(e.target.value)}>
                {academicYears.map(y => <option key={y.id} value={y.id}>{y.yearName}</option>)}
              </select>
            </label>
          </div>

          <form className="card" onSubmit={handleCreateStructure}>
            <h3>Define Fee for {academicYears.find(y => y.id === selectedYearId)?.yearName}</h3>
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
                Due Date
                <input type="date" value={structureForm.dueDate} onChange={e => setStructureForm({ ...structureForm, dueDate: e.target.value })} required />
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
                  <th>Due Date</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {structures.map(s => (
                  <tr key={s.id}>
                    <td>{s.feeCategoryName}</td>
                    <td>₹{s.amount.toLocaleString()}</td>
                    <td>{s.dueDate}</td>
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
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8, marginTop: 12 }}>
              {students.map(s => (
                <button
                  key={s.id}
                  className={`secondary ${selectedStudent?.id === s.id ? 'active' : ''}`}
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
                  <h3>Fees for {selectedStudent.firstName} {selectedStudent.lastName}</h3>
                  <div className="table-wrap">
                    <table className="module-table">
                      <thead>
                        <tr>
                          <th>Fee Head</th>
                          <th>Amount Due</th>
                          <th>Amount Paid</th>
                          <th>Balance</th>
                          <th>Status</th>
                          <th>Action</th>
                        </tr>
                      </thead>
                      <tbody>
                        {studentFees.map(sf => (
                          <tr key={sf.id}>
                            <td>{sf.feeCategoryName}</td>
                            <td>₹{sf.amountDue.toLocaleString()}</td>
                            <td>₹{sf.amountPaid.toLocaleString()}</td>
                            <td>₹{(sf.amountDue - sf.amountPaid).toLocaleString()}</td>
                            <td><span className={`badge ${sf.status === 'PAID' ? 'active' : sf.status === 'PARTIAL' ? 'warning' : 'danger'}`}>{sf.status}</span></td>
                            <td>
                              {sf.status !== 'PAID' && (
                                <button className="primary" style={{ padding: '6px 12px' }}>Collect</button>
                              )}
                            </td>
                          </tr>
                        ))}
                        {studentFees.length === 0 && (
                          <tr>
                            <td colSpan={6} style={{ textAlign: 'center' }}>No fees assigned to this student.</td>
                          </tr>
                        )}
                      </tbody>
                    </table>
                  </div>
                </div>
              </>
            ) : (
              <div className="empty-state">
                <p>Select a student from the left to manage their fee collection.</p>
              </div>
            )}
          </div>
        </div>
      )}
    </section>
  );
}
