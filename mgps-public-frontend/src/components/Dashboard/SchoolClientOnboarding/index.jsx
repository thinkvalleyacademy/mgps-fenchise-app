import React, { useState, useContext } from "react";
import { useNavigate } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import { Bounce } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { AuthContext } from "../../../context/student/AuthContext";
import { submitOnboardingRequest } from "../../../apis/SchoolOnboarding/schoolOnboardingService";

const onboardingGoals = [
  "New school setup",
  "Preschool onboarding",
  "K-12 school onboarding",
  "Branch expansion",
  "Data migration from existing system",
  "Demo and evaluation",
];

const schoolTypes = [
  "Preschool",
  "Primary School (K-5)",
  "Middle School (6-8)",
  "High School (9-12)",
  "K-12 Complete",
  "International School",
  "Special Needs School",
  "Vocational Training Center",
];

const SchoolClientOnboarding = () => {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();
  const normalizedUser = user ? String(user).toLowerCase() : "";

  const [submitting, setSubmitting] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [createdSchoolCode, setCreatedSchoolCode] = useState("");
  
  const [form, setForm] = useState({
    schoolName: "",
    subdomain: "",
    contactEmail: "",
    contactPhone: "",
    alternatePhone: "",
    address: "",
    city: "",
    state: "",
    pincode: "",
    country: "India",
    schoolType: schoolTypes[0],
    studentStrength: "",
    establishedYear: "",
    affiliationNumber: "",
    onboardingGoal: onboardingGoals[0],
    priority: "MEDIUM",
    notes: "",
    websiteUrl: "",
  });

  // Superadmin guard
  if (normalizedUser !== "superadmin") {
    return (
      <div className="container-fluid p-4">
        <div className="alert alert-danger" role="alert">
          <h4 className="alert-heading">Access Denied</h4>
          <p>
            This page is only available for superadmin users. School client onboarding requires
            superadmin privileges to create new school tenants in the system.
          </p>
          <hr />
          <p className="mb-0">
            <button className="btn btn-outline-danger" onClick={() => navigate("/Dashboard")}>
              Return to Dashboard
            </button>
          </p>
        </div>
      </div>
    );
  }

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
      autoClose: 5000,
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

    // Auto-generate subdomain from school name
    if (name === "schoolName" && !form.subdomain) {
      const subdomain = value
        .toLowerCase()
        .replace(/[^a-z0-9\s]/g, "")
        .replace(/\s+/g, "-")
        .substring(0, 50);
      setForm((prev) => ({ ...prev, subdomain }));
    }
  };

  const validateForm = () => {
    if (!form.schoolName || !form.contactEmail || !form.contactPhone) {
      notifyError("Please fill all required fields (School Name, Contact Email, Contact Phone).");
      return false;
    }

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.contactEmail)) {
      notifyError("Please enter a valid email address.");
      return false;
    }

    if (!/^\d{10,15}$/.test(form.contactPhone.replace(/[\s\-\(\)]/g, ""))) {
      notifyError("Please enter a valid phone number (10-15 digits).");
      return false;
    }

    if (!form.subdomain || form.subdomain.length < 3) {
      notifyError("Subdomain must be at least 3 characters.");
      return false;
    }

    if (!/^[a-z0-9\-]+$/.test(form.subdomain)) {
      notifyError("Subdomain can only contain lowercase letters, numbers, and hyphens.");
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setSubmitting(true);

    // Prepare the onboarding payload
    const onboardingData = {
      schoolName: form.schoolName,
      contactPerson: "Superadmin Setup",
      email: form.contactEmail,
      mobileNumber: form.contactPhone.replace(/\D/g, "").substring(0, 10),
      city: form.city,
      schoolType: form.schoolType,
      studentStrength: form.studentStrength ? parseInt(form.studentStrength) : null,
      onboardingGoal: form.onboardingGoal,
      notes: `School Type: ${form.schoolType}\nAddress: ${form.address}, ${form.city}, ${form.state} ${form.pincode}\nCountry: ${form.country}\nWebsite: ${form.websiteUrl || "N/A"}\nEstablished: ${form.establishedYear || "N/A"}\nAffiliation: ${form.affiliationNumber || "N/A"}\nStudent Strength: ${form.studentStrength || "N/A"}\nAlternate Phone: ${form.alternatePhone || "N/A"}\nPriority: ${form.priority}\nNotes: ${form.notes || "None"}`,
    };

    try {
      const response = await submitOnboardingRequest(onboardingData);

      if (response?.status === 200 || response?.data) {
        notifySuccess("School onboarding request created successfully!");
        setCreatedSchoolCode(response?.data?.schoolCode || "Will be generated upon approval");
        setSubmitted(true);
      } else {
        notifyError(response?.message || "Failed to create onboarding request.");
      }
    } catch (error) {
      console.error("Submission error:", error);
      notifyError("An error occurred while submitting the onboarding request.");
    } finally {
      setSubmitting(false);
    }
  };

  const styles = {
    page: {
      minHeight: "100vh",
      background: "linear-gradient(160deg, #f4efe6 0%, #e0f0ff 55%, #f7dcc4 100%)",
      padding: "24px 20px 48px",
    },
    container: {
      maxWidth: "1200px",
      margin: "0 auto",
    },
    header: {
      background: "linear-gradient(135deg, #17384b 0%, #255f7d 100%)",
      color: "#fff",
      padding: "24px 32px",
      borderRadius: "16px",
      marginBottom: "24px",
    },
    headerTitle: {
      fontSize: "32px",
      fontWeight: 800,
      margin: "0 0 8px",
    },
    headerSubtitle: {
      fontSize: "16px",
      margin: 0,
      opacity: 0.9,
    },
    panel: {
      background: "rgba(255,255,255,0.95)",
      border: "1px solid rgba(22, 66, 91, 0.08)",
      borderRadius: "20px",
      boxShadow: "0 24px 70px rgba(21, 52, 72, 0.14)",
      overflow: "hidden",
      marginBottom: "24px",
    },
    panelHeader: {
      padding: "20px 28px",
      background: "linear-gradient(140deg, #f8f2e8 0%, #e8f3ff 100%)",
      borderBottom: "1px solid rgba(23,56,75,0.1)",
    },
    panelTitle: {
      fontSize: "22px",
      fontWeight: 700,
      color: "#17384b",
      margin: 0,
    },
    formBody: {
      padding: "28px",
    },
    formGrid: {
      display: "grid",
      gridTemplateColumns: "repeat(auto-fit, minmax(280px, 1fr))",
      gap: "20px",
    },
    field: {
      display: "grid",
      gap: "8px",
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
      borderRadius: "12px",
      border: "1px solid rgba(23,56,75,0.15)",
      background: "#fff",
      padding: "12px 14px",
      fontSize: "15px",
      outline: "none",
      transition: "border-color 0.2s",
    },
    select: {
      width: "100%",
      borderRadius: "12px",
      border: "1px solid rgba(23,56,75,0.15)",
      background: "#fff",
      padding: "12px 14px",
      fontSize: "15px",
      outline: "none",
      cursor: "pointer",
    },
    textarea: {
      width: "100%",
      minHeight: "100px",
      borderRadius: "12px",
      border: "1px solid rgba(23,56,75,0.15)",
      background: "#fff",
      padding: "12px 14px",
      fontSize: "15px",
      outline: "none",
      resize: "vertical",
    },
    submit: {
      width: "100%",
      border: "none",
      borderRadius: "14px",
      background: "linear-gradient(135deg, #17384b 0%, #255f7d 100%)",
      color: "#fff",
      padding: "14px 18px",
      fontSize: "16px",
      fontWeight: 700,
      cursor: "pointer",
      boxShadow: "0 14px 28px rgba(23,56,75,0.18)",
      marginTop: "20px",
    },
    success: {
      background: "linear-gradient(140deg, #edfdf3 0%, #f7fff5 100%)",
      border: "1px solid #bbe2c6",
      borderRadius: "16px",
      padding: "32px",
      color: "#1c5a2f",
      textAlign: "center",
    },
    infoBox: {
      background: "linear-gradient(140deg, #e8f3ff 0%, #f0f8ff 100%)",
      border: "1px solid #b3d4fc",
      borderRadius: "12px",
      padding: "20px",
      color: "#17384b",
      marginBottom: "20px",
    },
    badge: {
      display: "inline-block",
      background: "#17384b",
      color: "#fff6e9",
      borderRadius: "999px",
      padding: "6px 12px",
      fontSize: "11px",
      fontWeight: 700,
      letterSpacing: "0.08em",
      textTransform: "uppercase",
      marginBottom: "12px",
    },
  };

  return (
    <>
      <div style={styles.page}>
        <div style={styles.container}>
          {/* Header */}
          <div style={styles.header}>
            <h1 style={styles.headerTitle}>School Client Onboarding</h1>
            <p style={styles.headerSubtitle}>
              Create a new school tenant in the MGPS system. This will generate a unique school code
              and provision the complete infrastructure for the new client.
            </p>
          </div>

          {submitted ? (
            <div style={styles.panel}>
              <div style={styles.formBody}>
                <div style={styles.success}>
                  <h2 style={{ marginTop: 0 }}>✓ School Onboarding Request Created</h2>
                  <p style={{ fontSize: "16px", lineHeight: 1.6 }}>
                    The school client has been successfully registered in the system.
                  </p>
                  <div style={styles.infoBox}>
                    <p style={{ margin: "0 0 8px", fontWeight: 700 }}>School Details:</p>
                    <p style={{ margin: 0 }}>
                      <strong>School Name:</strong> {form.schoolName}
                      <br />
                      <strong>Subdomain:</strong> {form.subdomain}
                      <br />
                      <strong>Contact Email:</strong> {form.contactEmail}
                      <br />
                      <strong>School Type:</strong> {form.schoolType}
                    </p>
                  </div>
                  <p>
                    The school code will be automatically generated and assigned when the
                    onboarding process is completed.
                  </p>
                  <div style={{ display: "flex", gap: "12px", justifyContent: "center", marginTop: "20px" }}>
                    <button
                      style={{ ...styles.submit, width: "auto", padding: "12px 24px" }}
                      onClick={() => navigate("/Dashboard")}
                    >
                      Return to Dashboard
                    </button>
                    <button
                      style={{ ...styles.submit, width: "auto", padding: "12px 24px", background: "linear-gradient(135deg, #2d6a4f 0%, #40916c 100%)" }}
                      onClick={() => {
                        setSubmitted(false);
                        setForm({
                          schoolName: "",
                          subdomain: "",
                          contactEmail: "",
                          contactPhone: "",
                          alternatePhone: "",
                          address: "",
                          city: "",
                          state: "",
                          pincode: "",
                          country: "India",
                          schoolType: schoolTypes[0],
                          studentStrength: "",
                          establishedYear: "",
                          affiliationNumber: "",
                          onboardingGoal: onboardingGoals[0],
                          priority: "MEDIUM",
                          notes: "",
                          websiteUrl: "",
                        });
                      }}
                    >
                      Onboard Another School
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ) : (
            <form onSubmit={handleSubmit}>
              {/* School Information */}
              <div style={styles.panel}>
                <div style={styles.panelHeader}>
                  <h2 style={styles.panelTitle}>School Information</h2>
                </div>
                <div style={styles.formBody}>
                  <div style={styles.formGrid}>
                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="schoolName">School Name *</label>
                      <input
                        id="schoolName"
                        name="schoolName"
                        style={styles.input}
                        value={form.schoolName}
                        onChange={handleChange}
                        placeholder="Mother's Goose Public School"
                        required
                      />
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="subdomain">Subdomain *</label>
                      <input
                        id="subdomain"
                        name="subdomain"
                        style={styles.input}
                        value={form.subdomain}
                        onChange={handleChange}
                        placeholder="mothers-goose-school"
                        required
                      />
                      <small style={{ color: "#5d7688", fontSize: "12px" }}>
                        This will be used for the school's unique URL (auto-generated from school name)
                      </small>
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="schoolType">School Type *</label>
                      <select
                        id="schoolType"
                        name="schoolType"
                        style={styles.select}
                        value={form.schoolType}
                        onChange={handleChange}
                      >
                        {schoolTypes.map((type) => (
                          <option key={type} value={type}>{type}</option>
                        ))}
                      </select>
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="studentStrength">Student Strength</label>
                      <input
                        id="studentStrength"
                        name="studentStrength"
                        type="number"
                        min="0"
                        style={styles.input}
                        value={form.studentStrength}
                        onChange={handleChange}
                        placeholder="e.g. 500"
                      />
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="establishedYear">Established Year</label>
                      <input
                        id="establishedYear"
                        name="establishedYear"
                        type="number"
                        min="1900"
                        max={new Date().getFullYear()}
                        style={styles.input}
                        value={form.establishedYear}
                        onChange={handleChange}
                        placeholder="e.g. 2005"
                      />
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="affiliationNumber">Affiliation Number</label>
                      <input
                        id="affiliationNumber"
                        name="affiliationNumber"
                        style={styles.input}
                        value={form.affiliationNumber}
                        onChange={handleChange}
                        placeholder="e.g. CBSE-12345"
                      />
                    </div>

                    <div style={{ ...styles.field, ...styles.full }}>
                      <label style={styles.label} htmlFor="websiteUrl">Website URL</label>
                      <input
                        id="websiteUrl"
                        name="websiteUrl"
                        type="url"
                        style={styles.input}
                        value={form.websiteUrl}
                        onChange={handleChange}
                        placeholder="https://www.school-website.com"
                      />
                    </div>
                  </div>
                </div>
              </div>

              {/* Contact Information */}
              <div style={styles.panel}>
                <div style={styles.panelHeader}>
                  <h2 style={styles.panelTitle}>Contact Information</h2>
                </div>
                <div style={styles.formBody}>
                  <div style={styles.formGrid}>
                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="contactEmail">Contact Email *</label>
                      <input
                        id="contactEmail"
                        name="contactEmail"
                        type="email"
                        style={styles.input}
                        value={form.contactEmail}
                        onChange={handleChange}
                        placeholder="principal@school.com"
                        required
                      />
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="contactPhone">Contact Phone *</label>
                      <input
                        id="contactPhone"
                        name="contactPhone"
                        style={styles.input}
                        value={form.contactPhone}
                        onChange={handleChange}
                        placeholder="+91 9876543210"
                        required
                      />
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="alternatePhone">Alternate Phone</label>
                      <input
                        id="alternatePhone"
                        name="alternatePhone"
                        style={styles.input}
                        value={form.alternatePhone}
                        onChange={handleChange}
                        placeholder="+91 9876543211"
                      />
                    </div>
                  </div>
                </div>
              </div>

              {/* Address */}
              <div style={styles.panel}>
                <div style={styles.panelHeader}>
                  <h2 style={styles.panelTitle}>Address</h2>
                </div>
                <div style={styles.formBody}>
                  <div style={styles.formGrid}>
                    <div style={{ ...styles.field, ...styles.full }}>
                      <label style={styles.label} htmlFor="address">Street Address</label>
                      <textarea
                        id="address"
                        name="address"
                        style={styles.textarea}
                        value={form.address}
                        onChange={handleChange}
                        placeholder="123 Main Street, Sector 5"
                      />
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="city">City</label>
                      <input
                        id="city"
                        name="city"
                        style={styles.input}
                        value={form.city}
                        onChange={handleChange}
                        placeholder="Lucknow"
                      />
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="state">State</label>
                      <input
                        id="state"
                        name="state"
                        style={styles.input}
                        value={form.state}
                        onChange={handleChange}
                        placeholder="Uttar Pradesh"
                      />
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="pincode">Pincode</label>
                      <input
                        id="pincode"
                        name="pincode"
                        style={styles.input}
                        value={form.pincode}
                        onChange={handleChange}
                        placeholder="226001"
                        maxLength="6"
                      />
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="country">Country</label>
                      <input
                        id="country"
                        name="country"
                        style={styles.input}
                        value={form.country}
                        onChange={handleChange}
                        placeholder="India"
                      />
                    </div>
                  </div>
                </div>
              </div>

              {/* Onboarding Details */}
              <div style={styles.panel}>
                <div style={styles.panelHeader}>
                  <h2 style={styles.panelTitle}>Onboarding Details</h2>
                </div>
                <div style={styles.formBody}>
                  <div style={styles.formGrid}>
                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="onboardingGoal">Onboarding Goal *</label>
                      <select
                        id="onboardingGoal"
                        name="onboardingGoal"
                        style={styles.select}
                        value={form.onboardingGoal}
                        onChange={handleChange}
                      >
                        {onboardingGoals.map((goal) => (
                          <option key={goal} value={goal}>{goal}</option>
                        ))}
                      </select>
                    </div>

                    <div style={styles.field}>
                      <label style={styles.label} htmlFor="priority">Priority</label>
                      <select
                        id="priority"
                        name="priority"
                        style={styles.select}
                        value={form.priority}
                        onChange={handleChange}
                      >
                        <option value="LOW">Low</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="HIGH">High</option>
                        <option value="URGENT">Urgent</option>
                      </select>
                    </div>

                    <div style={{ ...styles.field, ...styles.full }}>
                      <label style={styles.label} htmlFor="notes">Additional Notes</label>
                      <textarea
                        id="notes"
                        name="notes"
                        style={styles.textarea}
                        value={form.notes}
                        onChange={handleChange}
                        placeholder="Any special requirements, migration needs, preferred go-live date, etc."
                      />
                    </div>
                  </div>

                  <button type="submit" style={styles.submit} disabled={submitting}>
                    {submitting ? "Creating Onboarding Request..." : "Create School Client"}
                  </button>
                </div>
              </div>
            </form>
          )}
        </div>
      </div>
      <ToastContainer />
    </>
  );
};

export default SchoolClientOnboarding;
