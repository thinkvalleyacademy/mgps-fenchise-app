import React, {useEffect, useState} from 'react';
import axios from 'axios';

export default function SuperAdmin(){
  const [tenants, setTenants] = useState([]);
  const [error, setError] = useState(null);
  const [creating, setCreating] = useState(false);
  const [form, setForm] = useState({schoolCode:'', schoolName:'', subdomain:'', contactEmail:''});

  const handle = (e) => setForm({...form, [e.target.name]: e.target.value});
  const create = async (e) => {
    e.preventDefault();
    setCreating(true);
    try{
      await axios.post('/api/v1/onboarding/schools', form);
      const res = await axios.get('/api/v1/schools');
      setTenants(res.data);
    }catch(e){ setError(e.message); }
    setCreating(false);
  }

  useEffect(()=>{
    (async ()=>{
      try{
        const res = await axios.get('/api/v1/schools');
        setTenants(res.data);
      }catch(e){ setError(e.message); }
    })();
  },[]);

  return (
    <div style={{maxWidth:800, margin:'0 auto'}}>
      <h2>Super Admin — Tenants</h2>
      {error && <p style={{color:'red'}}>{error}</p>}
      <ul>
        {tenants.map(t => (
          <li key={t.id}>{t.schoolCode} — {t.schoolName} — {t.schemaName}</li>
        ))}
      </ul>
      <h3>Create Tenant</h3>
      <form onSubmit={create}>
        <div><input name="schoolCode" placeholder="schoolCode" value={form.schoolCode} onChange={handle}/></div>
        <div><input name="schoolName" placeholder="schoolName" value={form.schoolName} onChange={handle}/></div>
        <div><input name="subdomain" placeholder="subdomain" value={form.subdomain} onChange={handle}/></div>
        <div><input name="contactEmail" placeholder="contactEmail" value={form.contactEmail} onChange={handle}/></div>
        <div><button type="submit" disabled={creating}>{creating? 'Creating...':'Create'}</button></div>
      </form>
    </div>
  )
}
