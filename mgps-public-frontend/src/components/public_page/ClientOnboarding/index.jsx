import React, { useState } from "react";
import { Link } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import { Bounce } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import HomeNav from "../HomePage/homeNav/HomeNavChildFriendly";
import MyFooter from "../HomePage/footerComponent/MyFooterChildFriendly";
import { submitOnboardingRequest } from "../../../apis/SchoolOnboarding/schoolOnboardingService";

const onboardingOptions = [
  "New school setup",
  "Preschool onboarding",
  "K-12 school onboarding",
  "Branch expansion",
  "Data migration",
  "Demo request",
];

const ClientOnboarding = () => {
  const [submitting, setSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [form, setForm] = useState({
    schoolName: "",
    contactPerson: "",
    email: "",
    mobileNumber: "",
    city: "",
    schoolType: "",
    studentStrength: "",
    onboardingGoal: onboardingOptions[0],
    notes: "",
  });

  const notifyError = (msg) => {
    toast.error(msg, {
      position: "top-right",
      autoClose: 3500,
      hideProgressBar: true,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      theme: "colored",
      transition: Bounce,
    });
  };

  const notifySuccess = (msg) => {
    toast.success(msg, {
      position: "top-right",
      autoClose: 4500,
      hideProgressBar: true,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      theme: "colored",
      transition: Bounce,
    });
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!form.schoolName || !form.contactPerson || !form.email || !form.mobileNumber) {
      notifyError("Please fill all required fields.");
      return;
    }

    if (!/^\d{10}$/.test(form.mobileNumber)) {
      notifyError("Please enter a valid 10-digit mobile number.");
      return;
    }

    setSubmitting(true);
    
    // Prepare payload for new school onboarding API
    const payload = {
      schoolName: form.schoolName,
      contactPerson: form.contactPerson,
      email: form.email,
      mobileNumber: form.mobileNumber,
      city: form.city,
      schoolType: form.schoolType,
      studentStrength: form.studentStrength ? parseInt(form.studentStrength) : null,
      onboardingGoal: form.onboardingGoal,
      notes: form.notes,
    };

    const response = await submitOnboardingRequest(payload);
    setSubmitting(false);

    if (response?.status === 200 || response?.data) {
      notifySuccess("Onboarding request submitted. Our team will contact you soon.");
      setSubmitted(true);
      return;
    }

    notifyError(response?.message || "Unable to submit onboarding request right now.");
  };

  const styles = {
    page: {
      minHeight: "100vh",
      background: "linear-gradient(160deg, #f4efe6 0%, #e0f0ff 55%, #f7dcc4 100%)",
      padding: "88px 20px 48px",
    },
    shell: {
      maxWidth: "1120px",
      margin: "0 auto",
      display: "grid",
      gridTemplateColumns: "1.05fr 0.95fr",
      gap: "24px",
      alignItems: "start",
    },
    panel: {
      background: "rgba(255,255,255,0.9)",
      border: "1px solid rgba(22, 66, 91, 0.08)",
      borderRadius: "28px",
      boxShadow: "0 24px 70px rgba(21, 52, 72, 0.14)",
      overflow: "hidden",
    },
    hero: {
      padding: "38px 34px",
      color: "#17384b",
      background: "radial-gradient(circle at top left, rgba(255,203,119,0.35), transparent 40%), linear-gradient(140deg, #f8f2e8 0%, #e8f3ff 100%)",
    },
    eyebrow: {
      display: "inline-block",
      background: "#17384b",
      color: "#fff6e9",
      borderRadius: "999px",
      padding: "8px 14px",
      fontSize: "12px",
      fontWeight: 700,
      letterSpacing: "0.08em",
      textTransform: "uppercase",
      marginBottom: "16px",
    },
    title: {
      fontSize: "40px",
      lineHeight: 1.05,
      margin: "0 0 14px",
      fontWeight: 800,
    },
    lead: {
      fontSize: "17px",
      lineHeight: 1.7,
      color: "#315468",
      marginBottom: "26px",
    },
    statRow: {
      display: "grid",
      gridTemplateColumns: "repeat(3, 1fr)",
      gap: "14px",
      marginTop: "26px",
    },
    statCard: {
      background: "#fff",
      borderRadius: "18px",
      padding: "16px",
      border: "1px solid rgba(23,56,75,0.08)",
    },
    statLabel: {
      fontSize: "12px",
      textTransform: "uppercase",
      letterSpacing: "0.08em",
      color: "#8a6f3d",
      fontWeight: 700,
      marginBottom: "8px",
    },
    statValue: {
      fontSize: "18px",
      fontWeight: 800,
      color: "#17384b",
    },
    list: {
      marginTop: "28px",
      display: "grid",
      gap: "12px",
    },
    listItem: {
      display: "flex",
      alignItems: "center",
      gap: "12px",
      color: "#17384b",
      fontWeight: 600,
    },
    dot: {
      width: "12px",
      height: "12px",
      borderRadius: "999px",
      background: "linear-gradient(135deg, #d77b47 0%, #f4bf6d 100%)",
      flexShrink: 0,
    },
    formWrap: {
      padding: "30px",
    },
    formTitle: {
      fontSize: "28px",
      fontWeight: 800,
      color: "#17384b",
      margin: "0 0 8px",
    },
    formLead: {
      color: "#597487",
      marginBottom: "22px",
      lineHeight: 1.6,
    },
    formGrid: {
      display: "grid",
      gridTemplateColumns: "1fr 1fr",
      gap: "16px",
    },
    field: {
      display: "grid",
      gap: "8px",
      marginBottom: "16px",
    },
    full: {
      gridColumn: "1 / -1",
    },
    label: {
      fontSize: "13px",
      fontWeight: 700,
      color: "#17384b",
      textTransform: "uppercase",
      letterSpacing: "0.04em",
    },
    input: {
      width: "100%",
      borderRadius: "14px",
      border: "1px solid rgba(23,56,75,0.15)",
      background: "#fff",
      padding: "14px 15px",
      fontSize: "15px",
      outline: "none",
    },
    textarea: {
      width: "100%",
      minHeight: "110px",
      borderRadius: "14px",
      border: "1px solid rgba(23,56,75,0.15)",
      background: "#fff",
      padding: "14px 15px",
      fontSize: "15px",
      outline: "none",
      resize: "vertical",
    },
    submit: {
      width: "100%",
      border: "none",
      borderRadius: "16px",
      background: "linear-gradient(135deg, #17384b 0%, #255f7d 100%)",
      color: "#fff",
      padding: "15px 18px",
      fontSize: "16px",
      fontWeight: 800,
      cursor: "pointer",
      boxShadow: "0 14px 28px rgba(23,56,75,0.18)",
      marginTop: "6px",
    },
    helper: {
      marginTop: "14px",
      color: "#5d7688",
      fontSize: "14px",
      lineHeight: 1.6,
    },
    success: {
      background: "linear-gradient(140deg, #edfdf3 0%, #f7fff5 100%)",
      border: "1px solid #bbe2c6",
      borderRadius: "20px",
      padding: "26px",
      color: "#1c5a2f",
    },
  };

  return (
    <>
      <HomeNav />
      <div style={styles.page}>
        <div style={styles.shell}>
          <section style={styles.panel}>
            <div style={styles.hero}>
              <div style={styles.eyebrow}>Client Onboarding</div>
              <h1 style={styles.title}>Bring your school live on MGPS without the usual rollout chaos.</h1>
              <p style={styles.lead}>
                Use this page to start school onboarding for a new campus, preschool, or branch.
                Our team will review your setup goals, contact your point person, and plan the data,
                user, and launch flow with you.
              </p>

              <div style={styles.statRow}>
                <div style={styles.statCard}>
                  <div style={styles.statLabel}>Setup Scope</div>
                  <div style={styles.statValue}>Admissions, users, fees</div>
                </div>
                <div style={styles.statCard}>
                  <div style={styles.statLabel}>Onboarding Track</div>
                  <div style={styles.statValue}>Guided school launch</div>
                </div>
                <div style={styles.statCard}>
                  <div style={styles.statLabel}>Next Step</div>
                  <div style={styles.statValue}>Team callback</div>
                </div>
              </div>

              <div style={styles.list}>
                <div style={styles.listItem}><span style={styles.dot} />School profile and branch setup</div>
                <div style={styles.listItem}><span style={styles.dot} />Admin user provisioning and approvals</div>
                <div style={styles.listItem}><span style={styles.dot} />Migration planning for student and fee records</div>
                <div style={styles.listItem}><span style={styles.dot} />Go-live checklist for dev and production</div>
              </div>
            </div>
          </section>

          <section style={styles.panel}>
            <div style={styles.formWrap}>
              <h2 style={styles.formTitle}>Start School Onboarding</h2>
              <p style={styles.formLead}>
                Share the essentials. This goes to the onboarding queue so the team can respond with the
                right rollout plan.
              </p>

              {submitted ? (
                <div style={styles.success}>
                  <h3 style={{ marginTop: 0 }}>Request received</h3>
                  <p>
                    Your onboarding request has been submitted. Our team will contact you using the
                    details you provided.
                  </p>
                  <p style={{ marginBottom: 0 }}>
                    You can return to the <Link to="/" style={{ color: "#1c5a2f", fontWeight: 800 }}>homepage</Link> or open the
                    regular <Link to="/signup" style={{ color: "#1c5a2f", fontWeight: 800 }}>user signup</Link> flow.
                  </p>
                </div>
              ) : (
                <form onSubmit={handleSubmit}>
                  <div style={styles.formGrid}>
                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="schoolName">School Name *</label>
                      <input id="schoolName" name="schoolName" style={styles.input} value={form.schoolName} onChange={handleChange} placeholder="Mother's Goose Preschool" required />
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="contactPerson">Contact Person *</label>
                      <input id="contactPerson" name="contactPerson" style={styles.input} value={form.contactPerson} onChange={handleChange} placeholder="Owner / Principal / Admin lead" required />
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="email">Email *</label>
                      <input id="email" type="email" name="email" style={styles.input} value={form.email} onChange={handleChange} placeholder="school@example.com" required />
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="mobileNumber">Mobile Number *</label>
                      <input id="mobileNumber" name="mobileNumber" style={styles.input} value={form.mobileNumber} onChange={handleChange} placeholder="10-digit contact number" maxLength="10" required />
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="city">City</label>
                      <input id="city" name="city" style={styles.input} value={form.city} onChange={handleChange} placeholder="Lucknow" />
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="schoolType">School Type</label>
                      <input id="schoolType" name="schoolType" style={styles.input} value={form.schoolType} onChange={handleChange} placeholder="Preschool / K-12 / Branch" />
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="studentStrength">Approx. Student Strength</label>
                      <input id="studentStrength" name="studentStrength" style={styles.input} value={form.studentStrength} onChange={handleChange} placeholder="e.g. 350" />
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="onboardingGoal">Onboarding Goal</label>
                      <select id="onboardingGoal" name="onboardingGoal" style={styles.input} value={form.onboardingGoal} onChange={handleChange}>
                        {onboardingOptions.map((option) => (
                          <option key={option} value={option}>{option}</option>
                        ))}
                      </select>
                    </div>

                    <div style={{ ...styles.field, ...styles.full }}>
                      <label style={styles.label} htmlFor="notes">Notes</label>
                      <textarea
                        id="notes"
                        name="notes"
                        style={styles.textarea}
                        value={form.notes}
                        onChange={handleChange}
                        placeholder="Mention migration needs, preferred launch timeline, branch count, or anything the onboarding team should know."
                      />
                    </div>
                  </div>

                  <button type="submit" style={styles.submit} disabled={submitting}>
                    {submitting ? "Submitting..." : "Submit Onboarding Request"}
                  </button>

                  <p style={styles.helper}>
                    This route uses the existing public enquiry flow to create an onboarding intake request.
                    For direct user registration, continue to <Link to="/signup">/signup</Link>.
                  </p>
                </form>
              )}
            </div>
          </section>
        </div>
      </div>
      <MyFooter />
      <ToastContainer />
    </>
  );
};

export default ClientOnboarding;
