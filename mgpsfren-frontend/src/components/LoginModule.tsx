import { useState } from 'react';

interface LoginModuleProps {
  loginForm: { schoolCode: string; email: string; password: string };
  updateLoginField: (key: 'schoolCode' | 'email' | 'password', value: string) => void;
  handleLoginSubmit: (event: React.FormEvent<HTMLFormElement>) => void;
  isLoggingIn: boolean;
  loginError: string | null;
}

export default function LoginModule({
  loginForm,
  updateLoginField,
  handleLoginSubmit,
  isLoggingIn,
  loginError
}: LoginModuleProps) {
  const [view, setView] = useState<'login' | 'forgot-password' | 'reset-password'>('login');
  const [forgotEmail, setForgotEmail] = useState('');
  const [passwords, setPasswords] = useState({ new: '', confirm: '' });
  const [isSendingReset, setIsSendingReset] = useState(false);
  const [resetSuccess, setResetSuccess] = useState<string | null>(null);
  const [resetError, setResetError] = useState<string | null>(null);

  const handleForgotSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSendingReset(true);
    setResetError(null);
    setResetSuccess(null);
    
    // Simulate API call for now as per the spec
    try {
      // In a real app, we would call an API here
      await new Promise(resolve => setTimeout(resolve, 1000));
      setResetSuccess(`If an account exists for ${forgotEmail}, a reset link has been sent.`);
      // For demo purposes, let's allow jumping to reset
      setTimeout(() => setView('reset-password'), 2000);
    } catch (err) {
      setResetError('Unable to process request. Please try again.');
    } finally {
      setIsSendingReset(false);
    }
  };

  const handleResetSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (passwords.new !== passwords.confirm) {
      setResetError('Passwords do not match');
      return;
    }
    setIsSendingReset(true);
    setResetError(null);
    
    try {
      await new Promise(resolve => setTimeout(resolve, 1000));
      setResetSuccess('Password has been successfully reset. Redirecting to login...');
      setTimeout(() => {
        setResetSuccess(null);
        setView('login');
      }, 2000);
    } catch (err) {
      setResetError('Unable to reset password.');
    } finally {
      setIsSendingReset(false);
    }
  };

  if (view === 'reset-password') {
    return (
      <section className="card form-panel">
        <div className="card-header">
          <div>
            <p className="section-label">Authentication</p>
            <h3>Reset Password</h3>
          </div>
          <span className="badge">Security</span>
        </div>

        <form onSubmit={handleResetSubmit}>
          <div className="form-grid">
            <label className="full">
              New Password
              <input
                type="password"
                value={passwords.new}
                onChange={(e) => setPasswords({ ...passwords, new: e.target.value })}
                placeholder="Enter new password"
                required
              />
            </label>
            <label className="full">
              Confirm Password
              <input
                type="password"
                value={passwords.confirm}
                onChange={(e) => setPasswords({ ...passwords, confirm: e.target.value })}
                placeholder="Confirm new password"
                required
              />
            </label>
          </div>

          {resetError ? <p className="error">{resetError}</p> : null}
          {resetSuccess ? <p className="success">{resetSuccess}</p> : null}

          <div className="actions">
            <button type="submit" className="primary" disabled={isSendingReset}>
              {isSendingReset ? 'Resetting...' : 'Reset Password'}
            </button>
            <button type="button" className="secondary" onClick={() => setView('login')}>
              Cancel
            </button>
          </div>
        </form>
      </section>
    );
  }

  if (view === 'forgot-password') {
    return (
      <section className="card form-panel">
        <div className="card-header">
          <div>
            <p className="section-label">Authentication</p>
            <h3>Forgot Password</h3>
          </div>
          <span className="badge">Reset</span>
        </div>

        <form onSubmit={handleForgotSubmit}>
          <div className="form-grid">
            <label className="full">
              Username/Email
              <input
                type="email"
                value={forgotEmail}
                onChange={(e) => setForgotEmail(e.target.value)}
                placeholder="Enter your registered email"
                required
              />
            </label>
          </div>

          {resetError ? <p className="error">{resetError}</p> : null}
          {resetSuccess ? <p className="success">{resetSuccess}</p> : null}

          <div className="actions">
            <button type="submit" className="primary" disabled={isSendingReset}>
              {isSendingReset ? 'Sending...' : 'Send Reset Link'}
            </button>
            <button type="button" className="secondary" onClick={() => setView('login')}>
              Back to Login
            </button>
          </div>
        </form>
      </section>
    );
  }

  return (
    <section className="card form-panel">
      <div className="card-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <div className="logo-placeholder" style={{ width: 40, height: 40, background: 'var(--accent)', borderRadius: '10px' }}></div>
          <div>
            <p className="section-label">MGPS Ecosystem</p>
            <h3>{loginForm.schoolCode ? 'School Login' : 'Central Login'}</h3>
          </div>
        </div>
        <span className="badge">Secure</span>
      </div>

      <form onSubmit={handleLoginSubmit}>
        <div className="form-grid">
          <label className="full">
            School Code (Optional)
            <input
              value={loginForm.schoolCode}
              onChange={(event) => updateLoginField('schoolCode', event.target.value)}
              placeholder="e.g. MGPS-PUNE-01"
            />
            {loginForm.schoolCode && (
              <p className="hint" style={{ color: 'var(--accent)', marginTop: '4px' }}>
                Logging in to: <strong>Verified School Partition</strong>
              </p>
            )}
          </label>

          <label>
            Email / Username
            <input
              type="text"
              value={loginForm.email}
              onChange={(event) => updateLoginField('email', event.target.value)}
              placeholder="user@mgps.example"
              required
            />
          </label>

          <label>
            Password
            <input
              type="password"
              value={loginForm.password}
              onChange={(event) => updateLoginField('password', event.target.value)}
              placeholder="••••••••"
              required
            />
          </label>
        </div>

        {loginError ? <p className="error">{loginError}</p> : null}

        <div className="actions">
          <button type="submit" className="primary" disabled={isLoggingIn}>
            {isLoggingIn ? 'Verifying...' : 'Sign In'}
          </button>
          <button type="button" className="secondary" onClick={() => setView('forgot-password')}>
            Forgot password?
          </button>
        </div>
        
        <div style={{ marginTop: '24px', padding: '16px', borderRadius: '12px', background: 'rgba(255,255,255,0.02)', border: '1px solid rgba(255,255,255,0.05)' }}>
          <p className="hint" style={{ margin: 0, fontSize: '0.8rem' }}>
            <strong>Security Notice:</strong> This session is encrypted. If you are an admin of a franchise school, please provide your school code for direct access.
          </p>
        </div>
      </form>
    </section>
  );
}
