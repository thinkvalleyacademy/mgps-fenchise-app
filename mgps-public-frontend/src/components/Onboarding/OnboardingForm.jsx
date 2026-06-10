import React, { useState } from "react";
import axios from "axios";
import { FaArrowRight, FaCheckCircle, FaEnvelope, FaSchool } from "react-icons/fa";

const isBrowser = typeof window !== "undefined";
const BASE_URL = `${isBrowser ? window.location.origin : ""}/user-management`;

const getErrorMessage = (error) => {
  const data = error.response?.data;
  if (typeof data === "string") return data;
  if (data?.message) return data.message;
  if (data?.data && typeof data.data === "string") return data.data;
  return error.message || "Unable to create school.";
};

export default function OnboardingForm() {
  const [form, setForm] = useState({
    schoolCode: "MGPS",
    schoolName: "Mother Goose Preschool",
    subdomain: "mgps",
    contactEmail: "",
  });
  const [status, setStatus] = useState(null);
  const [statusType, setStatusType] = useState("info");
  const [errors, setErrors] = useState({});
  const [submitting, setSubmitting] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({
      ...prev,
      [name]: name === "schoolCode" ? value.toUpperCase() : value,
    }));
  };

  const submit = async (event) => {
    event.preventDefault();
    const nextErrors = {};
    if (!form.schoolCode.trim()) nextErrors.schoolCode = "Required";
    if (!form.schoolName.trim()) nextErrors.schoolName = "Required";
    if (form.contactEmail && !/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(form.contactEmail)) {
      nextErrors.contactEmail = "Invalid email";
    }
    setErrors(nextErrors);
    if (Object.keys(nextErrors).length) return;

    setSubmitting(true);
    setStatus("Creating your first school workspace...");
    setStatusType("info");

    try {
      const response = await axios.post(`${BASE_URL}/api/v1/onboarding/bootstrap-school`, form);
      setStatus(`School ${response.data.schoolCode} created. You can create the first superadmin now.`);
      setStatusType("success");
    } catch (error) {
      setStatus(getErrorMessage(error));
      setStatusType("error");
    } finally {
      setSubmitting(false);
    }
  };

  const styles = {
    page: {
      minHeight: "100vh",
      background: "linear-gradient(135deg, #eaf4f8 0%, #fff8ed 46%, #f7d9c9 100%)",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      padding: "32px 18px",
      color: "#17384b",
    },
    shell: {
      width: "100%",
      maxWidth: "980px",
      display: "grid",
      gridTemplateColumns: "minmax(0, 0.95fr) minmax(320px, 1.05fr)",
      background: "#ffffff",
      border: "1px solid rgba(23, 56, 75, 0.1)",
      borderRadius: "8px",
      boxShadow: "0 22px 65px rgba(23, 56, 75, 0.15)",
      overflow: "hidden",
    },
    intro: {
      background: "#17384b",
      color: "#fff",
      padding: "44px 38px",
      display: "flex",
      flexDirection: "column",
      justifyContent: "space-between",
      gap: "32px",
    },
    iconBox: {
      width: "52px",
      height: "52px",
      borderRadius: "8px",
      background: "#f4bf6d",
      color: "#17384b",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      fontSize: "24px",
      marginBottom: "24px",
    },
    eyebrow: {
      margin: "0 0 12px",
      color: "#f4bf6d",
      fontSize: "13px",
      fontWeight: 800,
      textTransform: "uppercase",
    },
    title: {
      margin: 0,
      fontSize: "34px",
      lineHeight: 1.12,
      fontWeight: 800,
      letterSpacing: 0,
    },
    copy: {
      margin: "18px 0 0",
      color: "#dcecf3",
      fontSize: "15px",
      lineHeight: 1.7,
    },
    steps: {
      display: "grid",
      gap: "14px",
      fontSize: "14px",
      color: "#edf6fa",
    },
    step: {
      display: "flex",
      alignItems: "center",
      gap: "10px",
    },
    formPanel: {
      padding: "42px 40px",
    },
    formTitle: {
      margin: "0 0 8px",
      fontSize: "25px",
      fontWeight: 800,
      color: "#17384b",
    },
    formLead: {
      margin: "0 0 28px",
      color: "#5d7688",
      lineHeight: 1.6,
      fontSize: "14px",
    },
    field: {
      marginBottom: "18px",
    },
    label: {
      display: "block",
      marginBottom: "8px",
      fontSize: "13px",
      fontWeight: 800,
      color: "#17384b",
    },
    input: {
      width: "100%",
      minHeight: "46px",
      border: "1px solid rgba(23, 56, 75, 0.18)",
      borderRadius: "8px",
      padding: "11px 13px",
      fontSize: "15px",
      color: "#17384b",
      outline: "none",
      boxSizing: "border-box",
      background: "#fbfdff",
    },
    error: {
      display: "block",
      marginTop: "6px",
      color: "#b42318",
      fontSize: "12px",
      fontWeight: 700,
    },
    button: {
      width: "100%",
      minHeight: "48px",
      border: "none",
      borderRadius: "8px",
      background: submitting ? "#8ca0ad" : "#d77b47",
      color: "#fff",
      fontSize: "15px",
      fontWeight: 800,
      cursor: submitting ? "not-allowed" : "pointer",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      gap: "10px",
      marginTop: "8px",
    },
    status: {
      marginTop: "18px",
      padding: "12px 14px",
      borderRadius: "8px",
      fontSize: "14px",
      fontWeight: 700,
      background: statusType === "success" ? "#e8f5ee" : statusType === "error" ? "#fff0ed" : "#e8f3ff",
      color: statusType === "success" ? "#17663a" : statusType === "error" ? "#b42318" : "#17384b",
      border: statusType === "success" ? "1px solid #b7dfc7" : statusType === "error" ? "1px solid #ffc9bd" : "1px solid #c8dfef",
    },
    nextLink: {
      display: "inline-flex",
      alignItems: "center",
      gap: "8px",
      marginTop: "18px",
      color: "#17384b",
      fontWeight: 800,
      textDecoration: "none",
    },
  };

  return (
    <main style={styles.page}>
      <section style={styles.shell}>
        <aside style={styles.intro}>
          <div>
            <div style={styles.iconBox}>
              <FaSchool />
            </div>
            <p style={styles.eyebrow}>First-time setup</p>
            <h1 style={styles.title}>Create the first school workspace</h1>
            <p style={styles.copy}>
              This one-time setup creates the initial MGPS school record so the first superadmin can register and sign in.
            </p>
          </div>
          <div style={styles.steps}>
            <div style={styles.step}><FaCheckCircle /> Create school code MGPS</div>
            <div style={styles.step}><FaCheckCircle /> Register superadmin account</div>
            <div style={styles.step}><FaCheckCircle /> Manage schools and users from dashboard</div>
          </div>
        </aside>

        <div style={styles.formPanel}>
          <h2 style={styles.formTitle}>Bootstrap School</h2>
          <p style={styles.formLead}>
            This form is available only before the first school exists. After that, school onboarding moves into the superadmin dashboard.
          </p>

          <form onSubmit={submit}>
            <div style={styles.field}>
              <label style={styles.label} htmlFor="schoolCode">School Code</label>
              <input id="schoolCode" name="schoolCode" value={form.schoolCode} onChange={handleChange} style={styles.input} />
              {errors.schoolCode && <span style={styles.error}>{errors.schoolCode}</span>}
            </div>

            <div style={styles.field}>
              <label style={styles.label} htmlFor="schoolName">School Name</label>
              <input id="schoolName" name="schoolName" value={form.schoolName} onChange={handleChange} style={styles.input} />
              {errors.schoolName && <span style={styles.error}>{errors.schoolName}</span>}
            </div>

            <div style={styles.field}>
              <label style={styles.label} htmlFor="subdomain">Subdomain</label>
              <input id="subdomain" name="subdomain" value={form.subdomain} onChange={handleChange} style={styles.input} />
            </div>

            <div style={styles.field}>
              <label style={styles.label} htmlFor="contactEmail">Contact Email</label>
              <input id="contactEmail" name="contactEmail" value={form.contactEmail} onChange={handleChange} style={styles.input} placeholder="admin@school.com" />
              {errors.contactEmail && <span style={styles.error}>{errors.contactEmail}</span>}
            </div>

            <button type="submit" style={styles.button} disabled={submitting}>
              {submitting ? "Creating..." : "Create First School"} <FaArrowRight />
            </button>
          </form>

          {status && <div style={styles.status}>{status}</div>}
          <a href="/signup" style={styles.nextLink}><FaEnvelope /> Create superadmin account</a>
        </div>
      </section>
    </main>
  );
}
