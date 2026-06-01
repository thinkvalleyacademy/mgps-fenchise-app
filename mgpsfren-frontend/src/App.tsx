import { useEffect, useMemo, useState } from 'react';
import CreateSchoolForm from './components/CreateSchoolForm';
import LoginModule from './components/LoginModule';
import FeeManagementModule from './components/FeeManagementModule';
import { buildSchoolPreview, checkSuperAdminStatus, createSchool, fetchSchools, fetchSubscriptionPlans, login, registerSuperAdmin, getSchool, updateSchool, deleteSchool, registerUser, fetchUsers, updateUser, deleteUser, setAuthToken, fetchAcademicYears, createAcademicYear, activateAcademicYear, fetchClasses, createClass, fetchSections, createSection, fetchSubjects, createSubject, admitStudent, fetchStudents, updateStudent, deleteStudent, onboardStaff, fetchStaff, updateStaff, deleteStaff, fetchFeeStructures } from './api';
import type {
  AuthProfile,
  SchoolRegistrationPayload,
  SchoolSummary,
  SubscriptionPlan,
  SuperAdminSetupPayload,
  SuperAdminStatus
} from './types';

const defaultPlans: SubscriptionPlan[] = [
  {
    planId: 'basic',
    planName: 'Launch',
    description: 'For a fresh franchise school getting ready to open its doors.',
    maxStudents: 500,
    maxStaff: 50,
    maxUsers: 80,
    monthlyPrice: '₹4,999'
  },
  {
    planId: 'growth',
    planName: 'Growth',
    description: 'For expanding schools that need more room to scale operations.',
    maxStudents: 1500,
    maxStaff: 150,
    maxUsers: 200,
    monthlyPrice: '₹12,999'
  },
  {
    planId: 'enterprise',
    planName: 'Enterprise',
    description: 'For larger franchises with advanced onboarding and reporting needs.',
    maxStudents: 5000,
    maxStaff: 500,
    maxUsers: 700,
    monthlyPrice: '₹29,999'
  }
];

const initialForm: SchoolRegistrationPayload = {
  name: 'MGPS Horizon School',
  adminEmail: 'admin@horizon.mgps.example',
  adminPhone: '9876543210',
  address: '42 Palm Avenue, Near Central Park',
  city: 'Pune',
  state: 'Maharashtra',
  postalCode: '411001',
  subscriptionPlanId: 'basic'
};

const initialSetupForm: SuperAdminSetupPayload = {
  firstName: 'MGPS',
  lastName: 'Admin',
  email: 'superadmin@mgps.example',
  phone: '9876543210',
  password: 'StrongPass!2026'
};

function formatDate(value?: string) {
  if (!value) return 'Just now';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return new Intl.DateTimeFormat('en-IN', {
    month: 'short',
    day: 'numeric',
    year: 'numeric'
  }).format(date);
}

const moduleDefinitions: Record<string, { description: string }> = {
  Dashboard: {
    description: 'Executive summary and KPI widgets for your role.'
  },
  'Academic Session': {
    description: 'Manage academic years and current active session.'
  },
  'Class Management': {
    description: 'Configure and manage school classes.'
  },
  'Section Management': {
    description: 'Manage class sections and student distribution.'
  },
  'Subject Management': {
    description: 'Define and assign subjects to classes.'
  },
  'School Management': {
    description: 'View, filter, and manage franchise school records.'
  },
  'User Management': {
    description: 'Manage users, roles, and access permissions within your tenant.'
  },
  'Subscription Management': {
    description: 'Review and manage subscription plans and billing status.'
  },
  'Audit Logs': {
    description: 'Track system activity and user actions across the franchise.'
  },
  Reports: {
    description: 'Access system reports and generate operational insights.'
  },
  'Student Management': {
    description: 'Manage student records, classes, and enrollment status.'
  },
  'Staff Management': {
    description: 'Manage staff profiles, assignments, and attendance.'
  },
  Fees: {
    description: 'Manage fee structures, student dues and collection.'
  },
  Timetable: {
    description: 'Create and review class timetable schedules and assignments.'
  },
  Attendance: {
    description: 'Track attendance and review daily attendance reports.'
  },
  'Academic Structure': {
    description: 'Create and manage your academic hierarchy and curriculum.'
  },
  Communication: {
    description: 'Manage announcements, notices and messaging.'
  }
};

function formatCurrency(value: number) {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0
  }).format(value);
}

export default function App() {
  const isSetupRoute = window.location.pathname === '/setup/superadmin';
  const [setupStatus, setSetupStatus] = useState<SuperAdminStatus | null>(null);
  const [setupForm, setSetupForm] = useState<SuperAdminSetupPayload>(initialSetupForm);
  const [setupError, setSetupError] = useState<string | null>(null);
  const [setupSuccess, setSetupSuccess] = useState<string | null>(null);
  const [isSetupSubmitting, setIsSetupSubmitting] = useState(false);
  const [authProfile, setAuthProfile] = useState<AuthProfile | null>(null);
  const [accessToken, setAccessToken] = useState<string | null>(null);
  const [loginForm, setLoginForm] = useState({ schoolCode: '', email: '', password: '' });
  const [loginError, setLoginError] = useState<string | null>(null);
  const [isLoggingIn, setIsLoggingIn] = useState(false);
  const [availableModules, setAvailableModules] = useState<string[]>([]);
  const [selectedModule, setSelectedModule] = useState<string>('Dashboard');
  const [schoolView, setSchoolView] = useState<'list' | 'create' | 'edit' | 'view'>('list');
  const [selectedSchoolId, setSelectedSchoolId] = useState<string | null>(null);
  const [detailTab, setDetailTab] = useState<'overview' | 'users' | 'subscription' | 'activity'>('overview');
  const [schoolUsers, setSchoolUsers] = useState<AuthProfile[]>([]);
  const [isUsersLoading, setIsUsersLoading] = useState(false);
  const [showUserForm, setShowUserForm] = useState(false);
  const [editingUserId, setEditingUserId] = useState<string | null>(null);
  const [userForm, setUserForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    role: 'SCHOOL_ADMIN'
  });

  const [schools, setSchools] = useState<SchoolSummary[]>([]);
  const [plans, setPlans] = useState<SubscriptionPlan[]>(defaultPlans);
  const [isLoadingSchools, setIsLoadingSchools] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (window.location.pathname === '/' && !isSetupRoute) {
      window.history.replaceState({}, '', '/login');
    }

    if (isSetupRoute) {
      checkSuperAdminStatus()
        .then((status) => setSetupStatus(status))
        .catch((err) => setSetupError(err instanceof Error ? err.message : 'Unable to determine super admin status'));
      return;
    }

    if (authProfile) {
      return;
    }

    let active = true;

    fetchSchools()
      .then((items) => {
        if (!active) return;
        setSchools(items.slice(0, 5));
      })
      .catch((err) => {
        if (!active) return;
        setError(err instanceof Error ? err.message : 'Unable to fetch schools');
      })
      .finally(() => {
        if (active) setIsLoadingSchools(false);
      });

    fetchSubscriptionPlans()
      .then((items) => {
        if (!active) return;
        if (items && items.length > 0) {
          // Map backend plans to SubscriptionPlan type if needed (e.g. id -> planId)
          const mapped = items.map(p => ({
            ...p,
            planId: (p as any).id || p.planId
          }));
          setPlans(mapped);
        }
      })
      .catch(err => {
        console.error('Failed to fetch subscription plans', err);
      });

    return () => {
      active = false;
    };
  }, [isSetupRoute, authProfile]);

  useEffect(() => {
    if (authProfile) {
      const modules = getModulesForRole(authProfile.role);
      setAvailableModules(modules);
      setSelectedModule((current) => (modules.includes(current) ? current : modules[0]));
      if (window.location.pathname !== '/dashboard') {
        window.history.replaceState({}, '', '/dashboard');
      }
    }
  }, [authProfile]);

  function getModulesForRole(role: string) {
    const moduleMap: Record<string, string[]> = {
      SUPER_ADMIN: ['Dashboard', 'School Management', 'User Management', 'Subscription Management', 'Audit Logs', 'Reports'],
      SCHOOL_ADMIN: ['Dashboard', 'Academic Session', 'Class Management', 'Section Management', 'Subject Management', 'User Management', 'Student Management', 'Staff Management', 'Fees', 'Attendance', 'Communication'],
      STAFF: ['Dashboard', 'Academic Structure', 'Attendance', 'Communication'],
      TEACHER: ['Dashboard', 'Timetable', 'Academic Structure', 'Communication'],
      STUDENT: ['Dashboard', 'Attendance', 'Communication', 'Reports']
    };

    return moduleMap[role] ?? ['Dashboard'];
  }

  const dashboardMetrics = useMemo(() => {
    const totalSchools = schools.length;
    const activeSchools = schools.filter((school) => school.status === 'ACTIVE').length;
    const suspendedSchools = schools.filter((school) => school.status === 'SUSPENDED').length;
    const inactiveSchools = schools.filter((school) => school.status === 'INACTIVE').length;
    const totalStudents = schools.reduce((sum, school) => sum + (school.subscriptionPlan?.maxStudents ?? 0), 0);
    const totalStaff = schools.reduce((sum, school) => sum + (school.subscriptionPlan?.maxStaff ?? 0), 0);
    const estimatedRevenue = totalSchools * 12500;

    return {
      totalSchools,
      activeSchools,
      suspendedSchools,
      inactiveSchools,
      totalStudents,
      totalStaff,
      estimatedRevenue
    };
  }, [schools]);

  const topSchools = useMemo(() => schools.slice(0, 3), [schools]);

  const selectedSchool = useMemo(() => {
    if (!selectedSchoolId) return null;
    return schools.find((s) => s.schoolId === selectedSchoolId) ?? null;
  }, [schools, selectedSchoolId]);

  function renderModuleContent() {
    // School Admin Specific Data Fetching (Simplified)
    const schoolId = authProfile?.schoolId;

    switch (selectedModule) {
      case 'Academic Session':
        return <AcademicSessionModule schoolId={schoolId!} />;
      case 'Class Management':
        return <ClassManagementModule schoolId={schoolId!} />;
      case 'Section Management':
        return <SectionManagementModule schoolId={schoolId!} />;
      case 'Subject Management':
        return <SubjectManagementModule schoolId={schoolId!} />;
      case 'Student Management':
        return <StudentManagementModule schoolId={schoolId!} />;
      case 'Staff Management':
        return <StaffManagementModule schoolId={schoolId!} />;
      case 'Fees':
        return <FeeManagementModule schoolId={schoolId!} />;
      case 'School Management':
        return (
          <section className="module-panel">
            <div className="module-header">
              <div>
                <p className="section-label">School Management</p>
                <h2>{schoolView === 'list' ? 'Franchise school registry' : schoolView === 'create' ? 'Onboard new school' : schoolView === 'edit' ? `Edit ${selectedSchool?.name}` : 'School details'}</h2>
              </div>
              <span className="badge">{schoolView === 'list' ? `${schools.length} schools` : schoolView.toUpperCase()}</span>
            </div>

            <div style={{ display: 'flex', gap: 12, marginBottom: 12 }}>
              <button type="button" className={`secondary ${schoolView === 'list' ? 'active' : ''}`} onClick={() => { setSchoolView('list'); setSelectedSchoolId(null); }}>
                List
              </button>
              <button type="button" className={`primary ${schoolView === 'create' ? 'active' : ''}`} onClick={() => { setSchoolView('create'); setSelectedSchoolId(null); }}>
                Create School
              </button>
            </div>

            {schoolView === 'list' && (
              schools.length === 0 ? (
                <div className="empty-state">
                  <strong>No schools available yet</strong>
                  <p>Once schools are registered, they will appear here with status, subscription and admin details.</p>
                </div>
              ) : (
                <div className="table-wrap">
                  <table className="module-table">
                    <thead>
                      <tr>
                        <th>School</th>
                        <th>Admin email</th>
                        <th>Status</th>
                        <th>Plan</th>
                        <th>Created</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {schools.map((school) => (
                        <tr key={school.schoolId}>
                          <td>{school.name}</td>
                          <td>{school.adminEmail}</td>
                          <td>
                            <span className={`status-pill ${school.status.toLowerCase()}`}>{school.status}</span>
                          </td>
                          <td>{school.subscriptionPlan?.planName ?? 'N/A'}</td>
                          <td>{formatDate(school.createdAt)}</td>
                          <td>
                            <div style={{ display: 'flex', gap: 4 }}>
                              <button type="button" className="secondary small" onClick={() => handleViewSchool(school.schoolId)}>View</button>
                              <button type="button" className="secondary small" onClick={() => handleInitiateEditSchool(school.schoolId)}>Edit</button>
                              <button type="button" className="secondary small error-text" onClick={() => handleDeleteSchool(school.schoolId)}>Delete</button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )
            )}

            {schoolView === 'create' && (
              <div className="card-form-wrap">
                <CreateSchoolForm
                  plans={plans}
                  submitting={isSubmitting}
                  onSubmit={handleCreateSchool}
                  onCancel={() => setSchoolView('list')}
                />
                {error && <p className="error" style={{ marginTop: 12 }}>{error}</p>}
              </div>
            )}

            {schoolView === 'edit' && selectedSchool && (
              <div className="card-form-wrap">
                <CreateSchoolForm
                  plans={plans}
                  submitting={isSubmitting}
                  submitLabel="Update school"
                  initial={{
                    name: selectedSchool.name,
                    adminEmail: selectedSchool.adminEmail,
                    adminPhone: selectedSchool.adminPhone ?? '',
                    address: '', // Simplified for now
                    city: selectedSchool.city ?? '',
                    state: selectedSchool.state ?? '',
                    postalCode: '',
                    subscriptionPlanId: selectedSchool.subscriptionPlan?.planId ?? plans[0].planId
                  }}
                  onSubmit={handleEditSchool}
                  onCancel={() => { setSchoolView('list'); setSelectedSchoolId(null); }}
                />
                {error && <p className="error" style={{ marginTop: 12 }}>{error}</p>}
              </div>
            )}

            {schoolView === 'view' && selectedSchool && (
              <div className="detail-view">
                <div style={{ display: 'flex', gap: 12, marginBottom: 24, borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
                  <button className={`nav-item ${detailTab === 'overview' ? 'active' : ''}`} onClick={() => setDetailTab('overview')} style={{ border: 'none', background: 'transparent', padding: '12px 0', marginRight: 24, borderBottom: detailTab === 'overview' ? '2px solid var(--accent)' : 'none', borderRadius: 0 }}>Overview</button>
                  <button className={`nav-item ${detailTab === 'users' ? 'active' : ''}`} onClick={() => setDetailTab('users')} style={{ border: 'none', background: 'transparent', padding: '12px 0', marginRight: 24, borderBottom: detailTab === 'users' ? '2px solid var(--accent)' : 'none', borderRadius: 0 }}>Users</button>
                  <button className={`nav-item ${detailTab === 'subscription' ? 'active' : ''}`} onClick={() => setDetailTab('subscription')} style={{ border: 'none', background: 'transparent', padding: '12px 0', marginRight: 24, borderBottom: detailTab === 'subscription' ? '2px solid var(--accent)' : 'none', borderRadius: 0 }}>Subscription</button>
                  <button className={`nav-item ${detailTab === 'activity' ? 'active' : ''}`} onClick={() => setDetailTab('activity')} style={{ border: 'none', background: 'transparent', padding: '12px 0', borderBottom: detailTab === 'activity' ? '2px solid var(--accent)' : 'none', borderRadius: 0 }}>Activity Logs</button>
                </div>

                {detailTab === 'overview' && (
                  <div className="detail-grid">
                    <div className="detail-item">
                      <label>School Name</label>
                      <p>{selectedSchool.name}</p>
                    </div>
                    <div className="detail-item">
                      <label>Status</label>
                      <p className={`status-pill ${selectedSchool.status.toLowerCase()}`}>{selectedSchool.status}</p>
                    </div>
                    <div className="detail-item">
                      <label>Admin Contact</label>
                      <p>{selectedSchool.adminEmail}</p>
                      <p>{selectedSchool.adminPhone}</p>
                    </div>
                    <div className="detail-item">
                      <label>Location</label>
                      <p>{selectedSchool.city}, {selectedSchool.state}</p>
                    </div>
                    <div className="detail-item">
                      <label>Subscription</label>
                      <p><strong>{selectedSchool.subscriptionPlan?.planName}</strong></p>
                      <p>{selectedSchool.subscriptionPlan?.monthlyPrice} / month</p>
                    </div>
                    <div className="detail-item">
                      <label>Database</label>
                      <p><code>{selectedSchool.databaseName}</code></p>
                    </div>
                    <div className="detail-item">
                      <label>Created Date</label>
                      <p>{formatDate(selectedSchool.createdAt)}</p>
                    </div>
                  </div>
                )}

                {detailTab === 'users' && (
                  <div className="users-panel">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
                      <h3>School Users</h3>
                      {!showUserForm && (
                        <button className="primary small" onClick={() => {
                          resetUserForm();
                          setShowUserForm(true);
                        }}>Add Admin User</button>
                      )}
                    </div>

                    {showUserForm ? (
                      <form onSubmit={handleCreateUser} className="card-form-wrap">
                        <div className="form-grid">
                          <label>
                            First Name
                            <input value={userForm.firstName} onChange={e => setUserForm({ ...userForm, firstName: e.target.value })} required />
                          </label>
                          <label>
                            Last Name
                            <input value={userForm.lastName} onChange={e => setUserForm({ ...userForm, lastName: e.target.value })} required />
                          </label>
                          <label>
                            Email
                            <input type="email" value={userForm.email} onChange={e => setUserForm({ ...userForm, email: e.target.value })} required />
                          </label>
                          <label>
                            Password
                            <input type="password" value={userForm.password} onChange={e => setUserForm({ ...userForm, password: e.target.value })} required={!editingUserId} disabled={Boolean(editingUserId)} placeholder={editingUserId ? 'Password unchanged' : ''} />
                          </label>
                          <label>
                            Role
                            <select value={userForm.role} onChange={e => setUserForm({ ...userForm, role: e.target.value })}>
                              <option value="SCHOOL_ADMIN">SCHOOL_ADMIN</option>
                              <option value="STAFF">STAFF</option>
                              <option value="TEACHER">TEACHER</option>
                              <option value="STUDENT">STUDENT</option>
                            </select>
                          </label>
                        </div>
                        {error && <p className="error">{error}</p>}
                        <div className="actions" style={{ marginTop: 16 }}>
                          <button type="submit" className="primary" disabled={isSubmitting}>
                            {isSubmitting ? 'Saving...' : editingUserId ? 'Update User' : 'Create User'}
                          </button>
                          <button type="button" className="secondary" onClick={() => {
                            resetUserForm();
                            setShowUserForm(false);
                          }}>Cancel</button>
                        </div>
                      </form>
                    ) : (
                      <div className="table-wrap">
                        <table className="module-table">
                          <thead>
                            <tr>
                              <th>Name</th>
                              <th>Email</th>
                              <th>Role</th>
                              <th>Status</th>
                              <th>Actions</th>
                            </tr>
                          </thead>
                          <tbody>
                            {isUsersLoading ? (
                              <tr><td colSpan={5}>Loading users...</td></tr>
                            ) : schoolUsers.length === 0 ? (
                              <tr><td colSpan={5}>No users found for this school.</td></tr>
                            ) : (
                              schoolUsers.map(user => (
                                <tr key={user.userId}>
                                  <td>{user.firstName} {user.lastName}</td>
                                  <td>{user.email}</td>
                                  <td><span className="badge">{user.role}</span></td>
                                  <td><span className={`status-pill ${user.status.toLowerCase()}`}>{user.status}</span></td>
                                  <td>
                                    <button className="secondary small" type="button" onClick={() => handleEditUser(user)}>Edit</button>
                                    <button className="secondary small error-text" type="button" onClick={() => handleDeleteUser(user.userId)}>Delete</button>
                                  </td>
                                </tr>
                              ))
                            )}
                          </tbody>
                        </table>
                      </div>
                    )}
                  </div>
                )}

                {(detailTab === 'subscription' || detailTab === 'activity') && (
                  <div className="empty-state">
                    <strong>Tab content coming soon</strong>
                    <p>This section is currently under development.</p>
                  </div>
                )}

                <div className="actions" style={{ marginTop: 32 }}>
                  <div style={{ display: 'flex', gap: 8 }}>
                    <button type="button" className="primary" onClick={() => handleInitiateEditSchool(selectedSchool.schoolId)}>
                      Edit School
                    </button>
                    <button type="button" className="secondary" onClick={() => setSchoolView('list')}>
                      Back to List
                    </button>
                  </div>
                  <button type="button" className="secondary error-text" onClick={() => handleDeleteSchool(selectedSchool.schoolId)}>
                    Delete School
                  </button>
                </div>
              </div>
            )}
          </section>
        );

      case 'Dashboard':
        return (
          <section className="module-panel">
            <div className="module-header">
              <div>
                <p className="section-label">Dashboard</p>
                <h2>Franchise summary and operational highlights</h2>
              </div>
              <span className="badge">Overview</span>
            </div>

            <div className="module-card-grid">
              <article className="module-card">
                <h4>Total schools</h4>
                <p>{dashboardMetrics.totalSchools} registered schools across the franchise.</p>
              </article>
              <article className="module-card">
                <h4>Active schools</h4>
                <p>{dashboardMetrics.activeSchools} schools currently operating.</p>
              </article>
              <article className="module-card">
                <h4>Attendance coverage</h4>
                <p>{dashboardMetrics.inactiveSchools} inactive / {dashboardMetrics.suspendedSchools} suspended records.</p>
              </article>
              <article className="module-card">
                <h4>Estimated revenue</h4>
                <p>{formatCurrency(dashboardMetrics.estimatedRevenue)}</p>
              </article>
            </div>

            <div className="module-card-grid" style={{ marginTop: '18px' }}>
              <article className="module-card">
                <h4>Students capacity</h4>
                <p>{dashboardMetrics.totalStudents.toLocaleString('en-IN')} seats across current subscriptions.</p>
              </article>
              <article className="module-card">
                <h4>Staff coverage</h4>
                <p>{dashboardMetrics.totalStaff.toLocaleString('en-IN')} staff capacity configured.</p>
              </article>
            </div>

            <div className="module-header" style={{ marginTop: '30px' }}>
              <div>
                <p className="section-label">Top schools</p>
                <h2>Recently loaded school records</h2>
              </div>
            </div>

            {topSchools.length === 0 ? (
              <div className="empty-state">
                <strong>No school records yet</strong>
                <p>School data will appear here after your first successful fetch from the backend.</p>
              </div>
            ) : (
              <div className="module-card-grid">
                {topSchools.map((school) => (
                  <article key={school.schoolId} className="module-card">
                    <h4>{school.name}</h4>
                    <p>{school.adminEmail}</p>
                    <div className="registry-meta">
                      <span>{school.status}</span>
                      <span>{school.subscriptionPlan?.planName ?? 'No plan'}</span>
                    </div>
                  </article>
                ))}
              </div>
            )}
          </section>
        );

      case 'User Management':
        return (
          <section className="module-panel">
            <div className="module-header">
              <div>
                <p className="section-label">User Management</p>
                <h2>Manage user roles and access</h2>
              </div>
              <span className="badge">Permissions</span>
            </div>

            <div className="module-card-grid">
              <article className="module-card">
                <h4>Role assignment</h4>
                <p>Grant or revoke access for school admins, staff, teachers and students.</p>
              </article>
              <article className="module-card">
                <h4>Account status</h4>
                <p>Activate, suspend or recover accounts as needed.</p>
              </article>
            </div>
          </section>
        );


      case 'Subscription Management':
        return (
          <section className="module-panel">
            <div className="module-header">
              <div>
                <p className="section-label">Subscription Management</p>
                <h2>Plan assignment and billing</h2>
              </div>
              <div style={{ display: 'flex', gap: 8 }}>
                <button className="primary small">Create Plan</button>
                <span className="badge">Subscriptions</span>
              </div>
            </div>

            <div className="module-card-grid">
              {plans.map((plan) => (
                <article key={plan.planId} className="module-card">
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 12 }}>
                    <h4>{plan.planName}</h4>
                    <span className="badge">{plan.planId.toUpperCase()}</span>
                  </div>
                  <p>{plan.description}</p>
                  <div style={{ margin: '16px 0', borderTop: '1px solid rgba(255,255,255,0.05)', paddingTop: 12 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                      <span>Students</span>
                      <strong>{plan.maxStudents?.toLocaleString()}</strong>
                    </div>
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                      <span>Staff</span>
                      <strong>{plan.maxStaff?.toLocaleString()}</strong>
                    </div>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <strong style={{ fontSize: '1.4rem', color: 'var(--gold)' }}>{plan.monthlyPrice}</strong>
                    <button className="secondary small">Edit</button>
                  </div>
                </article>
              ))}
            </div>
          </section>
        );

      case 'Audit Logs':
        return (
          <section className="module-panel">
            <div className="module-header">
              <div>
                <p className="section-label">Audit Logs</p>
                <h2>System activity tracking</h2>
              </div>
              <span className="badge">Activity</span>
            </div>

            <div style={{ display: 'flex', gap: 12, marginBottom: 20, flexWrap: 'wrap' }}>
              <select className="secondary" style={{ minWidth: 200 }}>
                <option>All Schools</option>
                {schools.map(s => <option key={s.schoolId}>{s.name}</option>)}
              </select>
              <input type="date" className="secondary" style={{ color: 'var(--text)' }} />
              <input type="text" className="secondary" placeholder="Search user or action..." style={{ flex: 1 }} />
              <button className="primary">Filter</button>
            </div>

            <div className="table-wrap">
              <table className="module-table">
                <thead>
                  <tr>
                    <th>Timestamp</th>
                    <th>User</th>
                    <th>School</th>
                    <th>Action</th>
                    <th>Details</th>
                  </tr>
                </thead>
                <tbody>
                  <tr style={{ opacity: 0.5 }}>
                    <td colSpan={5} style={{ textAlign: 'center', padding: '40px' }}>
                      <div className="empty-state" style={{ border: 'none', background: 'transparent' }}>
                        <strong>No activity logs found</strong>
                        <p>Logs will appear here once users begin interacting with the system.</p>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </section>
        );

      case 'Reports':
        return (
          <section className="module-panel">
            <div className="module-header">
              <div>
                <p className="section-label">Reports</p>
                <h2>Operational insights</h2>
              </div>
              <span className="badge">Analytics</span>
            </div>

            <div className="module-card-grid">
              <article className="module-card">
                <h4>School growth</h4>
                <p>Track expansion, subscriptions and activity across the franchise.</p>
              </article>
              <article className="module-card">
                <h4>Attendance summary</h4>
                <p>Review attendance trends and identify irregularities.</p>
              </article>
            </div>
          </section>
        );

      case 'Timetable':
      case 'Attendance':
      case 'Communication':
      case 'Academic Structure':
        return (
          <section className="module-panel">
            <div className="module-header">
              <div>
                <p className="section-label">{selectedModule}</p>
                <h2>{moduleDefinitions[selectedModule]?.description ?? 'Module details'}</h2>
              </div>
              <span className="badge">{selectedModule}</span>
            </div>

            <div className="module-card-grid">
              <article className="module-card">
                <h4>Overview</h4>
                <p>Build and manage the main features for the {selectedModule.toLowerCase()} workflow.</p>
              </article>
              <article className="module-card">
                <h4>Next action</h4>
                <p>Use this space to add quick create actions and list views for this module.</p>
              </article>
              <span className="badge">{selectedModule}</span>
            </div>
            <div className="empty-state">
              <strong>Module interface is under construction.</strong>
              <p>The selected module will be expanded here as part of ongoing UI development.</p>
            </div>
          </section>
        );
    }
  }

  function updateLoginField<K extends keyof typeof loginForm>(key: K, value: string) {
    setLoginForm((current) => ({ ...current, [key]: value }));
  }

  async function handleLoginSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setLoginError(null);
    setIsLoggingIn(true);

    try {
      const response = await login(loginForm);
      setAuthProfile(response.profile);
      setAccessToken(response.accessToken);
      setAuthToken(response.accessToken);
    } catch (err) {
      setLoginError(err instanceof Error ? err.message : 'Unable to login');
    } finally {
      setIsLoggingIn(false);
    }
  }

  function logout() {
    setAuthProfile(null);
    setAccessToken(null);
    setAuthToken(null);
    setLoginForm({ schoolCode: '', email: '', password: '' });
    window.history.replaceState({}, '', '/login');
  }

  function updateSetupField<K extends keyof SuperAdminSetupPayload>(key: K, value: SuperAdminSetupPayload[K]) {
    setSetupForm((current) => ({ ...current, [key]: value }));
  }

  async function handleCreateSchool(payload: SchoolRegistrationPayload) {
    setIsSubmitting(true);
    setError(null);
    try {
      await createSchool(payload);
      setSchoolView('list');
      const refreshed = await fetchSchools();
      setSchools(refreshed);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to create school');
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleEditSchool(payload: SchoolRegistrationPayload) {
    if (!selectedSchoolId) return;
    setIsSubmitting(true);
    setError(null);
    try {
      await updateSchool(selectedSchoolId, payload);
      setSchoolView('list');
      setSelectedSchoolId(null);
      const refreshed = await fetchSchools();
      setSchools(refreshed);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to update school');
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleDeleteSchool(schoolId: string) {
    if (!window.confirm('Are you sure you want to delete this school? This will remove all associated data.')) return;
    try {
      await deleteSchool(schoolId);
      const refreshed = await fetchSchools();
      setSchools(refreshed);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to delete school');
    }
  }

  function handleViewSchool(schoolId: string) {
    setSelectedSchoolId(schoolId);
    setSchoolView('view');
    setDetailTab('overview');
    loadSchoolUsers(schoolId);
  }

  async function loadSchoolUsers(schoolId: string) {
    setIsUsersLoading(true);
    try {
      const users = await fetchUsers(schoolId);
      setSchoolUsers(users);
    } catch (err) {
      console.error('Failed to load school users', err);
    } finally {
      setIsUsersLoading(false);
    }
  }

  async function handleCreateUser(e: React.FormEvent) {
    e.preventDefault();
    if (!selectedSchoolId) return;
    setIsSubmitting(true);
    try {
      if (editingUserId) {
        await updateUser(editingUserId, {
          firstName: userForm.firstName,
          lastName: userForm.lastName,
          email: userForm.email,
          role: userForm.role
        });
      } else {
        await registerUser({ ...userForm, schoolId: selectedSchoolId });
      }
      setShowUserForm(false);
      resetUserForm();
      loadSchoolUsers(selectedSchoolId);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to create user');
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleDeleteUser(userId: string) {
    if (!window.confirm('Delete this user account?')) return;
    if (!selectedSchoolId) return;
    try {
      await deleteUser(userId);
      loadSchoolUsers(selectedSchoolId);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Unable to delete user');
    }
  }

  function handleEditUser(user: AuthProfile) {
    setEditingUserId(user.userId);
    setUserForm({
      firstName: user.firstName || '',
      lastName: user.lastName || '',
      email: user.email || '',
      password: '',
      role: user.role || 'SCHOOL_ADMIN'
    });
    setShowUserForm(true);
  }

  function resetUserForm() {
    setEditingUserId(null);
    setUserForm({ firstName: '', lastName: '', email: '', password: '', role: 'SCHOOL_ADMIN' });
  }

  function handleInitiateEditSchool(schoolId: string) {
    setSelectedSchoolId(schoolId);
    setSchoolView('edit');
  }

  async function handleSuperAdminSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setIsSetupSubmitting(true);
    setSetupError(null);
    setSetupSuccess(null);

    try {
      const response = await registerSuperAdmin(setupForm);
      setSetupSuccess('Super admin account created successfully. Logging you in...');
      setSetupStatus({ hasSuperAdmin: true });
      
      // Auto-login
      setAuthProfile(response.profile);
      setAccessToken(response.accessToken);
      setAuthToken(response.accessToken);
    } catch (err) {
      setSetupError(err instanceof Error ? err.message : 'Unable to create super admin');
    } finally {
      setIsSetupSubmitting(false);
    }
  }

  if (isSetupRoute) {
    return (
      <div className="app-shell">
        <div className="ambient ambient-a" />
        <div className="ambient ambient-b" />

        <header className="topbar">
          <div>
            <p className="eyebrow">MGPS System Setup</p>
            <h1>Super Admin bootstrap</h1>
          </div>

          <div className="topbar-status">
            <span className="dot" />
            <span>{setupStatus?.hasSuperAdmin ? 'A Super Admin already exists' : 'Create the first Super Admin account'}</span>
          </div>
        </header>

        <main className="layout">
          <section className="card form-panel">
            <div className="card-header">
              <div>
                <p className="section-label">Hidden setup route</p>
                <h3>/setup/superadmin</h3>
              </div>
              <span className="badge">Bootstrap</span>
            </div>

            {setupStatus?.hasSuperAdmin ? (
              <div className="empty-state">
                <strong>Super Admin already configured</strong>
                <p>The system is already initialized. Please use the login flow to continue.</p>
              </div>
            ) : (
              <form onSubmit={handleSuperAdminSubmit}>
                <div className="form-grid">
                  <label>
                    First name
                    <input
                      value={setupForm.firstName}
                      onChange={(event) => updateSetupField('firstName', event.target.value)}
                      placeholder="First name"
                    />
                  </label>

                  <label>
                    Last name
                    <input
                      value={setupForm.lastName}
                      onChange={(event) => updateSetupField('lastName', event.target.value)}
                      placeholder="Last name"
                    />
                  </label>

                  <label>
                    Email
                    <input
                      type="email"
                      value={setupForm.email}
                      onChange={(event) => updateSetupField('email', event.target.value)}
                      placeholder="superadmin@mgps.example"
                    />
                  </label>

                  <label>
                    Phone
                    <input
                      value={setupForm.phone}
                      onChange={(event) => updateSetupField('phone', event.target.value)}
                      placeholder="9876543210"
                    />
                  </label>

                  <label className="full">
                    Password
                    <input
                      type="password"
                      value={setupForm.password}
                      onChange={(event) => updateSetupField('password', event.target.value)}
                      placeholder="Strong password"
                    />
                  </label>
                </div>

                {setupError ? <p className="error">{setupError}</p> : null}
                {setupSuccess ? <p className="success">{setupSuccess}</p> : null}

                <div className="actions">
                  <button type="submit" className="primary" disabled={isSetupSubmitting}>
                    {isSetupSubmitting ? 'Creating...' : 'Create Super Admin'}
                  </button>
                  <p className="hint">This route is hidden and intended for the first-time bootstrap only. After creation, use the login screen.</p>
                </div>
              </form>
            )}
          </section>
        </main>
      </div>
    );
  }

  if (!authProfile) {
    return (
      <div className="app-shell">
        <div className="ambient ambient-a" />
        <div className="ambient ambient-b" />

        <header className="topbar">
          <div>
            <p className="eyebrow">MGPS Franchise Console</p>
            <h1>Login to continue</h1>
          </div>

          <div className="topbar-status">
            <span className="dot" />
            <span>Enter your credentials to access the dashboard.</span>
          </div>
        </header>

        <main className="layout">
          <LoginModule
            loginForm={loginForm}
            updateLoginField={updateLoginField}
            handleLoginSubmit={handleLoginSubmit}
            isLoggingIn={isLoggingIn}
            loginError={loginError}
          />
        </main>
      </div>
    );
  }

  return (
    <div className="app-shell">
      <div className="ambient ambient-a" />
      <div className="ambient ambient-b" />

      <header className="topbar">
        <div>
          <p className="eyebrow">MGPS Franchise Console</p>
          <h1>Welcome back, {authProfile.firstName}</h1>
        </div>

        <div className="topbar-status">
          <span className="dot" />
          <span>{`Your role: ${authProfile.role}`}</span>
        </div>

        <div className="topbar-actions">
          <button type="button" className="secondary" onClick={logout}>
            Logout
          </button>
        </div>
      </header>

      <main className="dashboard-frame">
        <aside className="sidebar card">
          <div className="sidebar-header">
            <div>
              <p className="section-label">Navigation</p>
              <h3>Modules</h3>
            </div>
          </div>

          <div className="nav-list">
            {availableModules.map((moduleName) => (
              <button
                key={moduleName}
                type="button"
                className={`nav-item ${moduleName === selectedModule ? 'active' : ''}`}
                onClick={() => setSelectedModule(moduleName)}
              >
                <span>{moduleName}</span>
                <span>{moduleName === selectedModule ? '●' : '○'}</span>
              </button>
            ))}
          </div>

          <div className="sidebar-footer">
            <p className="section-label">Current module</p>
            <strong>{selectedModule}</strong>
          </div>
        </aside>

        <section className="module-shell">
          <div className="module-banner card">
            <div>
              <p className="section-label">{selectedModule}</p>
              <h2>{moduleDefinitions[selectedModule]?.description ?? 'Manage your selected workflow.'}</h2>
            </div>
            <span className="badge">{selectedModule}</span>
          </div>

          <div className="module-meta card">
            <div>
              <p className="section-label">User</p>
              <strong>{authProfile.role}</strong>
            </div>
            <div>
              <p className="section-label">Available modules</p>
              <strong>{availableModules.length}</strong>
            </div>
            <div>
              <p className="section-label">School</p>
              <strong>{authProfile.schoolId ?? 'Master'}</strong>
            </div>
          </div>

          {renderModuleContent()}
        </section>
      </main>

      <footer className="footer card">
        <div>
          <p className="section-label">MGPS UI</p>
          <strong>Continue building module screens one page at a time.</strong>
        </div>
        <span className="hint">Phase 1 screens are now scaffolded in the dashboard navigation.</span>
      </footer>
    </div>
  );
}

function StudentManagementModule({ schoolId }: { schoolId: string }) {
  const [students, setStudents] = useState<any[]>([]);
  const [classes, setClasses] = useState<any[]>([]);
  const [sections, setSections] = useState<any[]>([]);
  const [availableFees, setAvailableFees] = useState<any[]>([]);
  const [selectedFeeIds, setSelectedFeeIds] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingStudentId, setEditingStudentId] = useState<string | null>(null);
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    classId: '',
    sectionId: ''
  });

  useEffect(() => {
    fetchStudents(schoolId).then(setStudents).finally(() => setLoading(false));
    fetchClasses(schoolId).then(setClasses);
  }, [schoolId]);

  useEffect(() => {
    if (form.classId) {
      fetchSections(form.classId).then(setSections);
      fetchAcademicYears(schoolId).then(years => {
        const activeYear = years.find(y => y.isActive);
        if (activeYear) {
          fetchFeeStructures(schoolId, activeYear.yearId).then(structures => {
            const classFees = structures.filter((s: any) => s.classId === null || s.classId === form.classId);
            setAvailableFees(classFees);
            setSelectedFeeIds(classFees.filter((s: any) => s.isDefault).map((s: any) => s.id));
          });
        } else {
          setAvailableFees([]);
          setSelectedFeeIds([]);
        }
      });
    } else {
      setSections([]);
      setAvailableFees([]);
      setSelectedFeeIds([]);
    }
  }, [form.classId, schoolId]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      if (editingStudentId) {
        await updateStudent(editingStudentId, {
          firstName: form.firstName,
          lastName: form.lastName,
          email: form.email,
          phone: form.phone
        });
      } else {
        await admitStudent({
          ...form,
          schoolId,
          feeStructureIds: selectedFeeIds
        });
      }

      setShowForm(false);
      resetStudentForm();
      setSelectedFeeIds([]);
      fetchStudents(schoolId).then(setStudents);
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Failed to save student');
    }
  }

  function resetStudentForm() {
    setEditingStudentId(null);
    setForm({ firstName: '', lastName: '', email: '', phone: '', classId: '', sectionId: '' });
  }

  function handleEditStudent(student: any) {
    setEditingStudentId(student.studentId);
    setShowForm(true);
    setForm({
      firstName: student.firstName || '',
      lastName: student.lastName || '',
      email: student.email || '',
      phone: student.phone || '',
      classId: student.classId || '',
      sectionId: student.sectionId || ''
    });

    if (student.classId) {
      fetchSections(student.classId).then(setSections);
    }
  }

  async function handleDeleteStudent(studentId: string) {
    if (!window.confirm('Delete student record? This cannot be undone.')) return;
    try {
      await deleteStudent(studentId);
      fetchStudents(schoolId).then(setStudents);
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Failed to delete student');
    }
  }

  const toggleFee = (id: string) => {
    setSelectedFeeIds(prev => prev.includes(id) ? prev.filter(fid => fid !== id) : [...prev, id]);
  };

  return (
    <section className="module-panel">
      <div className="module-header">
        <div>
          <p className="section-label">Student Management</p>
          <h2>Student Directory</h2>
        </div>
        <button className="primary small" onClick={() => {
          if (showForm) {
            resetStudentForm();
            setShowForm(false);
          } else {
            setShowForm(true);
          }
        }}>{showForm ? 'Cancel' : 'New Admission'}</button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="card-form-wrap" style={{ marginBottom: 20 }}>
          <div className="form-grid">
            <label>First Name <input value={form.firstName} onChange={e => setForm({ ...form, firstName: e.target.value })} required /></label>
            <label>Last Name <input value={form.lastName} onChange={e => setForm({ ...form, lastName: e.target.value })} required /></label>
            <label>Email <input type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} required /></label>
            <label>Phone <input value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} /></label>
            {!editingStudentId && (
              <label>Class
                <select value={form.classId} onChange={e => setForm({ ...form, classId: e.target.value })} required>
                  <option value="">Select Class</option>
                  {classes.map(c => <option key={c.classId} value={c.classId}>{c.name}</option>)}
                </select>
              </label>
            )}
            {!editingStudentId && (
              <label>Section
                <select value={form.sectionId} onChange={e => setForm({ ...form, sectionId: e.target.value })} required>
                  <option value="">Select Section</option>
                  {sections.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
                </select>
              </label>
            )}
            {editingStudentId && (
              <div style={{ gridColumn: '1 / -1', padding: '12px 14px', background: 'rgba(255,255,255,0.04)', borderRadius: 8 }}>
                <p className="hint">Admission number and class/section cannot be changed from this quick edit form.</p>
              </div>
            )}
          </div>

          {form.classId && availableFees.length > 0 && !editingStudentId && (
            <div style={{ marginTop: 16 }}>
              <p className="section-label">Applicable Fees</p>
              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: 10, marginTop: 8 }}>
                {availableFees.map(fee => (
                  <label key={fee.id} style={{ display: 'flex', alignItems: 'center', gap: 8, padding: 8, background: 'rgba(255,255,255,0.03)', borderRadius: 4, cursor: 'pointer' }}>
                    <input type="checkbox" checked={selectedFeeIds.includes(fee.id)} onChange={() => toggleFee(fee.id)} />
                    <div>
                      <strong style={{ display: 'block', fontSize: '0.9rem' }}>{fee.feeCategoryName}</strong>
                      <span className="hint">₹{fee.amount.toLocaleString()}</span>
                    </div>
                  </label>
                ))}
              </div>
              <p className="hint" style={{ marginTop: 8 }}>Selected fees will be assigned to the student upon admission. Default fees are pre-selected.</p>
            </div>
          )}

          {form.classId && availableFees.length === 0 && (
              <div style={{ marginTop: 16, padding: 12, background: 'rgba(255,165,0,0.1)', borderRadius: 4, border: '1px solid rgba(255,165,0,0.2)' }}>
                  <p className="hint" style={{ color: 'var(--gold)' }}>
                      <strong>Note:</strong> No fee structures found for the selected class in the current active session. 
                      Please ensure a session is <strong>Activated</strong> in the 'Academic Session' module and fees are defined in 'Fees' &rarr; 'Structure'.
                  </p>
              </div>
          )}

          <button type="submit" className="primary" style={{ marginTop: 24 }}>
            {editingStudentId ? 'Update Student' : 'Confirm Admission'}
          </button>
        </form>
      )}

      <div className="table-wrap">
        <table className="module-table">
          <thead>
            <tr><th>Adm No</th><th>Name</th><th>Class</th><th>Email</th><th>Status</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={6}>Loading...</td></tr>
            ) : students.length === 0 ? (
              <tr><td colSpan={6}>No students enrolled.</td></tr>
            ) : (
              students.map(s => (
                <tr key={s.studentId}>
                  <td>{s.admissionNumber}</td>
                  <td>{s.firstName} {s.lastName}</td>
                  <td>{s.className || 'Unassigned'}{s.sectionName ? ` - ${s.sectionName}` : ''}</td>
                  <td>{s.email}</td>
                  <td><span className="status-pill active">{s.status || 'ACTIVE'}</span></td>
                  <td>
                    <button className="secondary small" type="button" onClick={() => handleEditStudent(s)}>Edit</button>
                    <button className="secondary small error-text" type="button" onClick={() => handleDeleteStudent(s.studentId)} style={{ marginLeft: 8 }}>Delete</button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}

function StaffManagementModule({ schoolId }: { schoolId: string }) {
  const [staff, setStaff] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [editingStaffId, setEditingStaffId] = useState<string | null>(null);
  const [form, setForm] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    payrollEmployeeId: '',
    designation: 'TEACHER',
    joiningDate: new Date().toISOString().split('T')[0]
  });

  useEffect(() => {
    fetchStaff(schoolId).then(setStaff).finally(() => setLoading(false));
  }, [schoolId]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      if (editingStaffId) {
        await updateStaff(editingStaffId, {
          firstName: form.firstName,
          lastName: form.lastName,
          email: form.email,
          phone: form.phone,
          designation: form.designation,
          payrollEmployeeId: form.payrollEmployeeId
        });
      } else {
        await onboardStaff({
          ...form,
          schoolId
        });
      }
      setShowForm(false);
      resetStaffForm();
      fetchStaff(schoolId).then(setStaff);
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Failed to save staff');
    }
  }

  function resetStaffForm() {
    setEditingStaffId(null);
    setForm({ firstName: '', lastName: '', email: '', phone: '', payrollEmployeeId: '', designation: 'TEACHER', joiningDate: new Date().toISOString().split('T')[0] });
  }

  function handleEditStaff(member: any) {
    setEditingStaffId(member.staffId);
    setShowForm(true);
    setForm({
      firstName: member.firstName || '',
      lastName: member.lastName || '',
      email: member.email || '',
      phone: member.phone || '',
      payrollEmployeeId: member.payrollEmployeeId || '',
      designation: member.designation || 'TEACHER',
      joiningDate: member.joiningDate ? new Date(member.joiningDate).toISOString().split('T')[0] : new Date().toISOString().split('T')[0]
    });
  }

  async function handleDeleteStaff(staffId: string) {
    if (!window.confirm('Delete this staff member? This will remove the staff record.')) return;
    try {
      await deleteStaff(staffId);
      fetchStaff(schoolId).then(setStaff);
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Failed to delete staff');
    }
  }

  return (
    <section className="module-panel">
      <div className="module-header">
        <div>
          <p className="section-label">Staff Management</p>
          <h2>Staff Directory</h2>
        </div>
        <button className="primary small" onClick={() => {
          if (showForm) {
            resetStaffForm();
            setShowForm(false);
          } else {
            setShowForm(true);
          }
        }}>{showForm ? 'Cancel' : 'Onboard Staff'}</button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="card-form-wrap" style={{ marginBottom: 20 }}>
          <div className="form-grid">
            <label>First Name <input value={form.firstName} onChange={e => setForm({ ...form, firstName: e.target.value })} required /></label>
            <label>Last Name <input value={form.lastName} onChange={e => setForm({ ...form, lastName: e.target.value })} required /></label>
            <label>Email <input type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} required /></label>
            <label>Phone <input value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} /></label>
            <label>Payroll ID (optional) <input value={form.payrollEmployeeId} onChange={e => setForm({ ...form, payrollEmployeeId: e.target.value })} /></label>
            <label>Designation
              <select value={form.designation} onChange={e => setForm({ ...form, designation: e.target.value })} required>
                <option value="TEACHER">TEACHER</option>
                <option value="PRINCIPAL">PRINCIPAL</option>
                <option value="STAFF">STAFF</option>
                <option value="ACCOUNTANT">ACCOUNTANT</option>
              </select>
            </label>
            <label>Joining Date <input type="date" value={form.joiningDate} onChange={e => setForm({ ...form, joiningDate: e.target.value })} required /></label>
          </div>
          {editingStaffId && (
            <div style={{ marginTop: 12, padding: 12, background: 'rgba(255,255,255,0.04)', borderRadius: 8 }}>
              <p className="hint">Employee code is generated automatically by the backend and cannot be changed here.</p>
            </div>
          )}
          <button type="submit" className="primary" style={{ marginTop: 12 }}>
            {editingStaffId ? 'Update Staff' : 'Complete Onboarding'}
          </button>
        </form>
      )}

      <div className="table-wrap">
        <table className="module-table">
          <thead>
            <tr><th>Emp ID</th><th>Name</th><th>Designation</th><th>Email</th><th>Joining Date</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={6}>Loading...</td></tr>
            ) : staff.length === 0 ? (
              <tr><td colSpan={6}>No staff onboarded.</td></tr>
            ) : (
              staff.map(s => (
                <tr key={s.staffId}>
                  <td>{s.employeeCode || s.employeeId}</td>
                  <td>{s.firstName} {s.lastName}</td>
                  <td><span className="badge">{s.designation || s.role || 'STAFF'}</span></td>
                  <td>{s.email}</td>
                  <td>{formatDate(s.joiningDate)}</td>
                  <td>
                    <button className="secondary small" type="button" onClick={() => handleEditStaff(s)}>Edit</button>
                    <button className="secondary small error-text" type="button" onClick={() => handleDeleteStaff(s.staffId)} style={{ marginLeft: 8 }}>Delete</button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}

// --- SUB-MODULE COMPONENTS (Phase 2) ---

function AcademicSessionModule({ schoolId }: { schoolId: string }) {
  const [sessions, setSessions] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ name: '', startDate: '', endDate: '' });

  useEffect(() => {
    fetchAcademicYears(schoolId).then(setSessions).finally(() => setLoading(false));
  }, [schoolId]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      await createAcademicYear({ ...form, schoolId });
      setShowForm(false);
      setForm({ name: '', startDate: '', endDate: '' });
      fetchAcademicYears(schoolId).then(setSessions);
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Failed to create session');
    }
  }

  async function handleActivate(yearId: string) {
    try {
      await activateAcademicYear(yearId, schoolId);
      fetchAcademicYears(schoolId).then(setSessions);
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Failed to activate session');
    }
  }

  return (
    <section className="module-panel">
      <div className="module-header">
        <div>
          <p className="section-label">Academic Session</p>
          <h2>Manage sessions</h2>
        </div>
        <button className="primary small" onClick={() => setShowForm(!showForm)}>{showForm ? 'Cancel' : 'Create Session'}</button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="card-form-wrap" style={{ marginBottom: 20 }}>
          <div className="form-grid">
            <label>Session Name <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} placeholder="2026-27" required /></label>
            <label>Start Date <input type="date" value={form.startDate} onChange={e => setForm({ ...form, startDate: e.target.value })} required /></label>
            <label>End Date <input type="date" value={form.endDate} onChange={e => setForm({ ...form, endDate: e.target.value })} required /></label>
          </div>
          <button type="submit" className="primary" style={{ marginTop: 12 }}>Save Session</button>
        </form>
      )}

      <div className="table-wrap">
        <table className="module-table">
          <thead>
            <tr><th>Name</th><th>Start Date</th><th>End Date</th><th>Status</th><th>Actions</th></tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={5}>Loading...</td></tr>
            ) : sessions.length === 0 ? (
              <tr><td colSpan={5}>No sessions found.</td></tr>
            ) : (
              sessions.map(s => (
                <tr key={s.yearId}>
                  <td>{s.name}</td>
                  <td>{s.startDate}</td>
                  <td>{s.endDate}</td>
                  <td><span className={`status-pill ${s.isActive ? 'active' : 'inactive'}`}>{s.isActive ? 'Active' : 'Inactive'}</span></td>
                  <td>
                    {!s.isActive && <button className="secondary small" onClick={() => handleActivate(s.yearId)}>Activate</button>}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}

function ClassManagementModule({ schoolId }: { schoolId: string }) {
  const [classes, setClasses] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ name: '', code: '' });

  useEffect(() => {
    fetchClasses(schoolId).then(setClasses).finally(() => setLoading(false));
  }, [schoolId]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      await createClass({ ...form, schoolId });
      setShowForm(false);
      setForm({ name: '', code: '' });
      fetchClasses(schoolId).then(setClasses);
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Failed to create class');
    }
  }

  return (
    <section className="module-panel">
      <div className="module-header">
        <div>
          <p className="section-label">Class Management</p>
          <h2>School classes</h2>
        </div>
        <button className="primary small" onClick={() => setShowForm(!showForm)}>{showForm ? 'Cancel' : 'Create Class'}</button>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="card-form-wrap" style={{ marginBottom: 20 }}>
          <div className="form-grid">
            <label>Class Name <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} placeholder="Grade 10" required /></label>
            <label>Class Code <input value={form.code} onChange={e => setForm({ ...form, code: e.target.value })} placeholder="G10" required /></label>
          </div>
          <button type="submit" className="primary" style={{ marginTop: 12 }}>Save Class</button>
        </form>
      )}

      <div className="table-wrap">
        <table className="module-table">
          <thead>
            <tr><th>Name</th><th>Code</th><th>Sections</th></tr>
          </thead>
          <tbody>
            {loading ? (
              <tr><td colSpan={3}>Loading...</td></tr>
            ) : classes.length === 0 ? (
              <tr><td colSpan={3}>No classes found.</td></tr>
            ) : (
              classes.map(c => (
                <tr key={c.classId}>
                  <td>{c.name}</td>
                  <td>{c.code}</td>
                  <td>{c.sectionCount ?? 0} sections</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}

function SectionManagementModule({ schoolId }: { schoolId: string }) {
  const [classes, setClasses] = useState<any[]>([]);
  const [sections, setSections] = useState<any[]>([]);
  const [selectedClassId, setSelectedClassId] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ name: '', capacity: 40 });

  useEffect(() => {
    fetchClasses(schoolId).then(setClasses);
  }, [schoolId]);

  useEffect(() => {
    if (selectedClassId) {
      fetchSections(selectedClassId).then(setSections);
    } else {
      setSections([]);
    }
  }, [selectedClassId]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      await createSection({ ...form, classId: selectedClassId, schoolId });
      setShowForm(false);
      setForm({ name: '', capacity: 40 });
      fetchSections(selectedClassId).then(setSections);
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Failed to create section');
    }
  }

  return (
    <section className="module-panel">
      <div className="module-header">
        <div>
          <p className="section-label">Section Management</p>
          <h2>Class sections</h2>
        </div>
        <div style={{ display: 'flex', gap: 12 }}>
          <select className="secondary" value={selectedClassId} onChange={e => setSelectedClassId(e.target.value)}>
            <option value="">Select Class</option>
            {classes.map(c => <option key={c.classId} value={c.classId}>{c.name}</option>)}
          </select>
          {selectedClassId && (
            <button className="primary small" onClick={() => setShowForm(!showForm)}>{showForm ? 'Cancel' : 'Create Section'}</button>
          )}
        </div>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="card-form-wrap" style={{ marginBottom: 20 }}>
          <div className="form-grid">
            <label>Section Name <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} placeholder="Section A" required /></label>
            <label>Capacity <input type="number" value={form.capacity} onChange={e => setForm({ ...form, capacity: parseInt(e.target.value) })} required /></label>
          </div>
          <button type="submit" className="primary" style={{ marginTop: 12 }}>Save Section</button>
        </form>
      )}

      <div className="table-wrap">
        <table className="module-table">
          <thead>
            <tr><th>Section Name</th><th>Capacity</th></tr>
          </thead>
          <tbody>
            {!selectedClassId ? (
              <tr><td colSpan={2}>Please select a class.</td></tr>
            ) : sections.length === 0 ? (
              <tr><td colSpan={2}>No sections found for this class.</td></tr>
            ) : (
              sections.map(s => (
                <tr key={s.id}>
                  <td>{s.name}</td>
                  <td>{s.capacity ?? 0}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}

function SubjectManagementModule({ schoolId }: { schoolId: string }) {
  const [classes, setClasses] = useState<any[]>([]);
  const [subjects, setSubjects] = useState<any[]>([]);
  const [selectedClassId, setSelectedClassId] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ name: '', code: '', subjectType: 'CORE' });

  useEffect(() => {
    fetchClasses(schoolId).then(setClasses);
  }, [schoolId]);

  useEffect(() => {
    if (selectedClassId) {
      fetchSubjects(selectedClassId).then(setSubjects);
    } else {
      setSubjects([]);
    }
  }, [selectedClassId]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    try {
      await createSubject({ ...form, classId: selectedClassId, schoolId });
      setShowForm(false);
      setForm({ name: '', code: '', subjectType: 'CORE' });
      fetchSubjects(selectedClassId).then(setSubjects);
    } catch (err) {
      alert(err instanceof Error ? err.message : 'Failed to create subject');
    }
  }

  return (
    <section className="module-panel">
      <div className="module-header">
        <div>
          <p className="section-label">Subject Management</p>
          <h2>Class subjects</h2>
        </div>
        <div style={{ display: 'flex', gap: 12 }}>
          <select className="secondary" value={selectedClassId} onChange={e => setSelectedClassId(e.target.value)}>
            <option value="">Select Class</option>
            {classes.map(c => <option key={c.classId} value={c.classId}>{c.name}</option>)}
          </select>
          {selectedClassId && (
            <button className="primary small" onClick={() => setShowForm(!showForm)}>{showForm ? 'Cancel' : 'Create Subject'}</button>
          )}
        </div>
      </div>

      {showForm && (
        <form onSubmit={handleSubmit} className="card-form-wrap" style={{ marginBottom: 20 }}>
          <div className="form-grid">
            <label>Subject Name <input value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} placeholder="Mathematics" required /></label>
            <label>Subject Code <input value={form.code} onChange={e => setForm({ ...form, code: e.target.value })} placeholder="MATH10" required /></label>
            <label>Type 
              <select value={form.subjectType} onChange={e => setForm({ ...form, subjectType: e.target.value })}>
                <option value="CORE">CORE</option>
                <option value="ELECTIVE">ELECTIVE</option>
                <option value="PRACTICAL">PRACTICAL</option>
              </select>
            </label>
          </div>
          <button type="submit" className="primary" style={{ marginTop: 12 }}>Save Subject</button>
        </form>
      )}

      <div className="table-wrap">
        <table className="module-table">
          <thead>
            <tr><th>Name</th><th>Code</th><th>Type</th></tr>
          </thead>
          <tbody>
            {!selectedClassId ? (
              <tr><td colSpan={3}>Please select a class.</td></tr>
            ) : subjects.length === 0 ? (
              <tr><td colSpan={3}>No subjects found for this class.</td></tr>
            ) : (
              subjects.map(s => (
                <tr key={s.id}>
                  <td>{s.name}</td>
                  <td>{s.code}</td>
                  <td>{s.subjectType}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </section>
  );
}
