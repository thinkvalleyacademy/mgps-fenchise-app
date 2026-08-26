import React, { useEffect, useMemo, useState } from 'react';
import type { SchoolRegistrationPayload, SubscriptionPlan } from '../types';

type Props = {
  initial?: SchoolRegistrationPayload;
  plans: SubscriptionPlan[];
  submitting?: boolean;
  submitLabel?: string;
  onCancel?: () => void;
  onSubmit: (payload: SchoolRegistrationPayload) => Promise<void>;
  onCreatePlan?: () => void;
};

export default function CreateSchoolForm({ initial, plans, submitting, submitLabel = 'Create school', onCancel, onSubmit, onCreatePlan }: Props) {
  const [form, setForm] = useState<SchoolRegistrationPayload>(initial ?? ({} as SchoolRegistrationPayload));
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    setForm(initial ?? ({} as SchoolRegistrationPayload));
    setErrors({});
  }, [initial]);

  const preview = useMemo(() => {
    const name = form.name ?? '';
    const slug = name
      .trim()
      .toLowerCase()
      .replace(/[^a-z0-9]+/g, '-')
      .replace(/^-+|-+$/g, '');

    return {
      databaseName: slug ? `mgps_${slug}` : 'mgps_school_db',
      subdomain: slug ? `${slug}.smsapp.com` : 'school.smsapp.com'
    };
  }, [form.name]);

  function updateField<K extends keyof SchoolRegistrationPayload>(key: K, value: SchoolRegistrationPayload[K]) {
    setForm((cur) => ({ ...cur, [key]: value }));
  }

  function handleLogoChange(event: React.ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0];
    if (!file) return;

    if (!file.type.startsWith('image/')) {
      setErrors((cur) => ({ ...cur, logoUrl: 'Please upload a valid image file' }));
      return;
    }

    if (file.size > 750 * 1024) {
      setErrors((cur) => ({ ...cur, logoUrl: 'Logo must be 750 KB or smaller' }));
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      updateField('logoUrl', String(reader.result));
      setErrors((cur) => {
        const next = { ...cur };
        delete next.logoUrl;
        return next;
      });
    };
    reader.readAsDataURL(file);
  }

  function validate(): boolean {
    const e: Record<string, string> = {};
    if (!form.name || String(form.name).trim() === '') e.name = 'School name is required';
    if (!form.adminEmail || !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(String(form.adminEmail))) e.adminEmail = 'A valid admin email is required';
    if (!form.logoUrl || String(form.logoUrl).trim() === '') e.logoUrl = 'School logo is required';
    setErrors(e);
    return Object.keys(e).length === 0;
  }

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();
    if (!validate()) return;
    await onSubmit(form);
  }

  return (
    <form onSubmit={handleSubmit}>
      <div className="form-grid">
        <label>
          School name
          <input value={form.name ?? ''} onChange={(e) => updateField('name', e.target.value)} placeholder="School name" />
          {errors.name ? <small className="error">{errors.name}</small> : null}
        </label>

        <label>
          Admin email
          <input type="email" value={form.adminEmail ?? ''} onChange={(e) => updateField('adminEmail', e.target.value)} placeholder="admin@school.example" />
          {errors.adminEmail ? <small className="error">{errors.adminEmail}</small> : null}
        </label>

        <label>
          Admin phone
          <input value={form.adminPhone ?? ''} onChange={(e) => updateField('adminPhone', e.target.value)} placeholder="9876543210" />
        </label>

        <label className="full">
          Address
          <textarea rows={3} value={form.address ?? ''} onChange={(e) => updateField('address', e.target.value)} placeholder="School campus address" />
        </label>

        <label>
          City
          <input value={form.city ?? ''} onChange={(e) => updateField('city', e.target.value)} placeholder="City" />
        </label>

        <label>
          State
          <input value={form.state ?? ''} onChange={(e) => updateField('state', e.target.value)} placeholder="State" />
        </label>

        <label>
          Postal code
          <input value={form.postalCode ?? ''} onChange={(e) => updateField('postalCode', e.target.value)} placeholder="411001" />
        </label>

        <label className="full">
          School logo
          <input type="file" accept="image/*" onChange={handleLogoChange} />
          {form.logoUrl ? (
            <img src={form.logoUrl} alt="School logo preview" style={{ width: 72, height: 72, objectFit: 'contain', marginTop: 10, borderRadius: 8, background: 'rgba(255,255,255,0.06)', padding: 6 }} />
          ) : null}
          {errors.logoUrl ? <small className="error">{errors.logoUrl}</small> : null}
        </label>

        <label className="full">
          Subscription plan
          {plans.length > 0 ? (
            <div className="plan-options">
              {plans.map((plan) => (
                <button key={plan.planId} type="button" className={`plan-option ${plan.planId === form.subscriptionPlanId ? 'active' : ''}`} onClick={() => updateField('subscriptionPlanId', plan.planId)}>
                  <strong>{plan.planName}</strong>
                  <span>{plan.monthlyPrice}</span>
                  <small>{plan.maxStudents?.toLocaleString('en-IN')} students</small>
                </button>
              ))}
            </div>
          ) : (
            <div className="empty-state" style={{ padding: 16 }}>
              <p>No subscription plans available yet.</p>
              {onCreatePlan ? (
                <button type="button" className="primary small" onClick={onCreatePlan}>
                  Create new plan
                </button>
              ) : null}
            </div>
          )}
        </label>
      </div>

      <div className="actions">
        <button type="submit" className="primary" disabled={submitting}>{submitting ? 'Submitting...' : submitLabel}</button>
        {onCancel ? (
          <button type="button" className="secondary" onClick={onCancel} style={{ marginLeft: 8 }}>
            Cancel
          </button>
        ) : null}
        <p className="hint">Preview: <code>{preview.subdomain}</code> • DB: <code>{preview.databaseName}</code></p>
      </div>
    </form>
  );
}
