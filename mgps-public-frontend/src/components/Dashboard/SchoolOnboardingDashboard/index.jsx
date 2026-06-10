import React, { useState, useEffect, useContext, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import { Bounce } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { AuthContext } from "../../../context/student/AuthContext";
import {
  getAllOnboardingRequests,
  updateOnboardingStatus,
  assignToAdmin,
  updatePriority,
  approveAndCreateSchool,
} from "../../../apis/SchoolOnboarding/schoolOnboardingService";

const SchoolOnboardingDashboard = () => {
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();
  const normalizedUser = user ? String(user).toLowerCase() : "";

  const [onboardingRequests, setOnboardingRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filterStatus, setFilterStatus] = useState("ALL");
  const [selectedRequest, setSelectedRequest] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [showApproveModal, setShowApproveModal] = useState(false);
  const [schoolCodeInput, setSchoolCodeInput] = useState("");
  const [approving, setApproving] = useState(false);

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
      autoClose: 3000,
      hideProgressBar: true,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      theme: "colored",
      transition: Bounce,
    });
  };

  const fetchOnboardingRequests = useCallback(async () => {
    setLoading(true);
    try {
      const token = localStorage.getItem("authToken");
      if (!token) {
        notifyError("Please login as superadmin");
        navigate("/login");
        return;
      }

      let response;
      if (filterStatus === "ALL") {
        response = await getAllOnboardingRequests(token);
      } else {
        // We'll need to add this API endpoint
        response = await getAllOnboardingRequests(token);
      }

      if (response?.data) {
        setOnboardingRequests(Array.isArray(response.data) ? response.data : []);
      } else {
        notifyError(response?.message || "Failed to fetch onboarding requests");
      }
    } catch (error) {
      console.error("Fetch error:", error);
      if (error.response?.status === 401 || error.response?.status === 403) {
        notifyError("Unauthorized. Please login as superadmin");
        navigate("/login");
      } else {
        notifyError("Failed to load onboarding requests");
      }
    } finally {
      setLoading(false);
    }
  }, [navigate, filterStatus]);

  useEffect(() => {
    if (normalizedUser === "superadmin") {
      fetchOnboardingRequests();
    }
  }, [fetchOnboardingRequests, normalizedUser]);

  const handleStatusUpdate = async (id, newStatus) => {
    try {
      const token = localStorage.getItem("authToken");
      const response = await updateOnboardingStatus(token, id, newStatus);

      if (response?.status === 200 || response?.data) {
        notifySuccess(`Status updated to ${newStatus}`);
        fetchOnboardingRequests();
        setShowModal(false);
        setSelectedRequest(null);
      } else {
        notifyError(response?.message || "Failed to update status");
      }
    } catch (error) {
      console.error("Update status error:", error);
      notifyError("Failed to update status");
    }
  };

  const handleAssignToAdmin = async (id, adminUsername) => {
    try {
      const token = localStorage.getItem("authToken");
      const response = await assignToAdmin(token, id, adminUsername);

      if (response?.status === 200 || response?.data) {
        notifySuccess("Assigned to admin successfully");
        fetchOnboardingRequests();
      } else {
        notifyError(response?.message || "Failed to assign to admin");
      }
    } catch (error) {
      console.error("Assign error:", error);
      notifyError("Failed to assign to admin");
    }
  };

  const handlePriorityUpdate = async (id, priority) => {
    try {
      const token = localStorage.getItem("authToken");
      const response = await updatePriority(token, id, priority);

      if (response?.status === 200 || response?.data) {
        notifySuccess("Priority updated");
        fetchOnboardingRequests();
      } else {
        notifyError(response?.message || "Failed to update priority");
      }
    } catch (error) {
      console.error("Priority update error:", error);
      notifyError("Failed to update priority");
    }
  };

  const handleApprove = async () => {
    if (!schoolCodeInput || schoolCodeInput.trim().length < 3) {
      notifyError("School code must be at least 3 characters");
      return;
    }

    setApproving(true);
    try {
      const token = localStorage.getItem("authToken");
      const response = await approveAndCreateSchool(token, selectedRequest.id, schoolCodeInput, user);

      if (response?.status === 200 || response?.data) {
        notifySuccess(`School approved with code: ${schoolCodeInput}`);
        fetchOnboardingRequests();
        setShowApproveModal(false);
        setSchoolCodeInput("");
      } else {
        notifyError(response?.message || "Failed to approve school");
      }
    } catch (error) {
      console.error("Approve error:", error);
      notifyError("Failed to approve and create school");
    } finally {
      setApproving(false);
    }
  };

  const getStatusBadge = (status) => {
    const statusColors = {
      PENDING: { bg: "#fff3cd", text: "#856404", border: "#ffc107" },
      IN_PROGRESS: { bg: "#cce5ff", text: "#004085", border: "#007bff" },
      COMPLETED: { bg: "#d4edda", text: "#155724", border: "#28a745" },
      REJECTED: { bg: "#f8d7da", text: "#721c24", border: "#dc3545" },
    };
    const colors = statusColors[status] || statusColors.PENDING;
    return (
      <span
        style={{
          display: "inline-block",
          padding: "4px 12px",
          borderRadius: "12px",
          background: colors.bg,
          color: colors.text,
          border: `1px solid ${colors.border}`,
          fontSize: "12px",
          fontWeight: 700,
        }}
      >
        {status}
      </span>
    );
  };

  const getPriorityBadge = (priority) => {
    const priorityColors = {
      LOW: { bg: "#e2e3e5", text: "#383d41", border: "#6c757d" },
      MEDIUM: { bg: "#d1ecf1", text: "#0c5460", border: "#17a2b8" },
      HIGH: { bg: "#fff3cd", text: "#856404", border: "#ffc107" },
      URGENT: { bg: "#f8d7da", text: "#721c24", border: "#dc3545" },
    };
    const colors = priorityColors[priority] || priorityColors.MEDIUM;
    return (
      <span
        style={{
          display: "inline-block",
          padding: "4px 12px",
          borderRadius: "12px",
          background: colors.bg,
          color: colors.text,
          border: `1px solid ${colors.border}`,
          fontSize: "12px",
          fontWeight: 700,
        }}
      >
        {priority}
      </span>
    );
  };

  const filteredRequests =
    filterStatus === "ALL"
      ? onboardingRequests
      : onboardingRequests.filter((req) => req.status === filterStatus);

  const styles = {
    page: {
      minHeight: "100vh",
      background: "linear-gradient(160deg, #f4efe6 0%, #e0f0ff 55%, #f7dcc4 100%)",
      padding: "24px 20px 48px",
    },
    container: {
      maxWidth: "1400px",
      margin: "0 auto",
    },
    header: {
      background: "linear-gradient(135deg, #17384b 0%, #255f7d 100%)",
      color: "#fff",
      padding: "24px 32px",
      borderRadius: "16px",
      marginBottom: "24px",
      display: "flex",
      justifyContent: "space-between",
      alignItems: "center",
    },
    headerTitle: {
      fontSize: "32px",
      fontWeight: 800,
      margin: 0,
    },
    headerActions: {
      display: "flex",
      gap: "12px",
      alignItems: "center",
    },
    filterSelect: {
      padding: "8px 16px",
      borderRadius: "8px",
      border: "1px solid rgba(255,255,255,0.3)",
      background: "rgba(255,255,255,0.1)",
      color: "#fff",
      fontSize: "14px",
      cursor: "pointer",
    },
    panel: {
      background: "rgba(255,255,255,0.95)",
      border: "1px solid rgba(22, 66, 91, 0.08)",
      borderRadius: "20px",
      boxShadow: "0 24px 70px rgba(21, 52, 72, 0.14)",
      overflow: "hidden",
    },
    table: {
      width: "100%",
      borderCollapse: "collapse",
    },
    th: {
      background: "linear-gradient(140deg, #f8f2e8 0%, #e8f3ff 100%)",
      padding: "16px",
      textAlign: "left",
      fontSize: "13px",
      fontWeight: 700,
      color: "#17384b",
      textTransform: "uppercase",
      letterSpacing: "0.04em",
      borderBottom: "2px solid rgba(23,56,75,0.1)",
    },
    td: {
      padding: "16px",
      borderBottom: "1px solid rgba(23,56,75,0.05)",
      fontSize: "14px",
      color: "#17384b",
    },
    actionButton: {
      padding: "6px 12px",
      borderRadius: "6px",
      border: "none",
      fontSize: "12px",
      fontWeight: 600,
      cursor: "pointer",
      marginRight: "8px",
    },
    modal: {
      position: "fixed",
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      background: "rgba(0,0,0,0.5)",
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
      zIndex: 1000,
    },
    modalContent: {
      background: "#fff",
      borderRadius: "16px",
      padding: "32px",
      maxWidth: "600px",
      width: "90%",
      maxHeight: "80vh",
      overflow: "auto",
    },
  };

  // Superadmin guard
  if (normalizedUser !== "superadmin") {
    return (
      <div className="container-fluid p-4">
        <div className="alert alert-danger" role="alert">
          <h4 className="alert-heading">Access Denied</h4>
          <p>This page is only available for superadmin users.</p>
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

  return (
    <>
      <div style={styles.page}>
        <div style={styles.container}>
          {/* Header */}
          <div style={styles.header}>
            <h1 style={styles.headerTitle}>School Onboarding Requests</h1>
            <div style={styles.headerActions}>
              <select
                style={styles.filterSelect}
                value={filterStatus}
                onChange={(e) => setFilterStatus(e.target.value)}
              >
                <option value="ALL">All Status</option>
                <option value="PENDING">Pending</option>
                <option value="IN_PROGRESS">In Progress</option>
                <option value="COMPLETED">Completed</option>
                <option value="REJECTED">Rejected</option>
              </select>
              <button
                style={{ ...styles.actionButton, background: "#fff", color: "#17384b" }}
                onClick={fetchOnboardingRequests}
              >
                Refresh
              </button>
            </div>
          </div>

          {/* Table */}
          <div style={styles.panel}>
            {loading ? (
              <div style={{ padding: "40px", textAlign: "center" }}>Loading...</div>
            ) : filteredRequests.length === 0 ? (
              <div style={{ padding: "40px", textAlign: "center", color: "#5d7688" }}>
                No onboarding requests found.
              </div>
            ) : (
              <div style={{ overflowX: "auto" }}>
                <table style={styles.table}>
                  <thead>
                    <tr>
                      <th style={styles.th}>School Name</th>
                      <th style={styles.th}>Contact</th>
                      <th style={styles.th}>Type</th>
                      <th style={styles.th}>Status</th>
                      <th style={styles.th}>Priority</th>
                      <th style={styles.th}>Created</th>
                      <th style={styles.th}>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {filteredRequests.map((request) => (
                      <tr key={request.id}>
                        <td style={styles.td}>
                          <strong>{request.schoolName}</strong>
                        </td>
                        <td style={styles.td}>
                          {request.email}
                          <br />
                          <small style={{ color: "#5d7688" }}>{request.mobileNumber}</small>
                        </td>
                        <td style={styles.td}>{request.schoolType}</td>
                        <td style={styles.td}>{getStatusBadge(request.status)}</td>
                        <td style={styles.td}>{getPriorityBadge(request.priority)}</td>
                        <td style={styles.td}>
                          {new Date(request.createdAt).toLocaleDateString()}
                        </td>
                        <td style={styles.td}>
                          <button
                            style={{ ...styles.actionButton, background: "#28a745", color: "#fff" }}
                            onClick={() => handleStatusUpdate(request.id, "IN_PROGRESS")}
                            disabled={request.status === "IN_PROGRESS" || request.status === "COMPLETED"}
                          >
                            Start
                          </button>
                          <button
                            style={{ ...styles.actionButton, background: "#007bff", color: "#fff" }}
                            onClick={() => {
                              setSelectedRequest(request);
                              setShowModal(true);
                            }}
                          >
                            View
                          </button>
                          {request.status === "PENDING" || request.status === "IN_PROGRESS" ? (
                            <button
                              style={{ ...styles.actionButton, background: "#28a745", color: "#fff", fontWeight: 700 }}
                              onClick={() => {
                                setSelectedRequest(request);
                                setShowApproveModal(true);
                              }}
                            >
                              Approve
                            </button>
                          ) : null}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Detail Modal */}
      {showModal && selectedRequest && (
        <div style={styles.modal} onClick={() => setShowModal(false)}>
          <div style={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <h2 style={{ marginTop: 0, color: "#17384b" }}>Onboarding Request Details</h2>
            <div style={{ marginBottom: "20px" }}>
              <p>
                <strong>School Name:</strong> {selectedRequest.schoolName}
              </p>
              <p>
                <strong>Contact Person:</strong> {selectedRequest.contactPerson}
              </p>
              <p>
                <strong>Email:</strong> {selectedRequest.email}
              </p>
              <p>
                <strong>Mobile:</strong> {selectedRequest.mobileNumber}
              </p>
              <p>
                <strong>City:</strong> {selectedRequest.city || "N/A"}
              </p>
              <p>
                <strong>School Type:</strong> {selectedRequest.schoolType || "N/A"}
              </p>
              <p>
                <strong>Student Strength:</strong> {selectedRequest.studentStrength || "N/A"}
              </p>
              <p>
                <strong>Onboarding Goal:</strong> {selectedRequest.onboardingGoal || "N/A"}
              </p>
              <p>
                <strong>Status:</strong> {getStatusBadge(selectedRequest.status)}
              </p>
              <p>
                <strong>Priority:</strong> {getPriorityBadge(selectedRequest.priority)}
              </p>
              <p>
                <strong>Notes:</strong>
                <br />
                {selectedRequest.notes || "No notes provided"}
              </p>
            </div>

            <div style={{ display: "flex", gap: "12px", flexWrap: "wrap" }}>
              <button
                style={{ ...styles.actionButton, background: "#28a745", color: "#fff", padding: "10px 20px" }}
                onClick={() => {
                  setShowModal(false);
                  setShowApproveModal(true);
                }}
                disabled={selectedRequest.status === "COMPLETED"}
              >
                ✓ Approve & Create School
              </button>
              <button
                style={{ ...styles.actionButton, background: "#dc3545", color: "#fff", padding: "10px 20px" }}
                onClick={() => handleStatusUpdate(selectedRequest.id, "REJECTED")}
                disabled={selectedRequest.status === "REJECTED"}
              >
                ✕ Reject
              </button>
              <button
                style={{ ...styles.actionButton, background: "#6c757d", color: "#fff", padding: "10px 20px" }}
                onClick={() => setShowModal(false)}
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Approve Modal */}
      {showApproveModal && selectedRequest && (
        <div style={styles.modal} onClick={() => setShowApproveModal(false)}>
          <div style={styles.modalContent} onClick={(e) => e.stopPropagation()}>
            <h2 style={{ marginTop: 0, color: "#17384b" }}>Approve School Onboarding</h2>
            <div style={{ marginBottom: "20px" }}>
              <div style={{ background: "#e8f3ff", padding: "16px", borderRadius: "8px", marginBottom: "16px" }}>
                <p style={{ margin: 0, fontWeight: 700 }}>School: {selectedRequest.schoolName}</p>
                <p style={{ margin: "8px 0 0", fontSize: "14px", color: "#5d7688" }}>
                  This will create a new school tenant in the system with a unique school code.
                </p>
              </div>

              <div style={{ marginBottom: "16px" }}>
                <label style={{ display: "block", marginBottom: "8px", fontWeight: 700, color: "#17384b" }}>
                  School Code *
                </label>
                <input
                  type="text"
                  value={schoolCodeInput}
                  onChange={(e) => setSchoolCodeInput(e.target.value.toUpperCase())}
                  placeholder="e.g., MGPS-001, SCH-2025-001"
                  style={{
                    width: "100%",
                    padding: "12px",
                    borderRadius: "8px",
                    border: "1px solid rgba(23,56,75,0.15)",
                    fontSize: "15px",
                  }}
                  maxLength={20}
                />
                <small style={{ color: "#5d7688", fontSize: "12px" }}>
                  This code will be used to identify the school in the system. Must be unique.
                </small>
              </div>

              <div style={{ display: "flex", gap: "12px", flexWrap: "wrap" }}>
                <button
                  style={{ ...styles.actionButton, background: "#28a745", color: "#fff", padding: "12px 24px", fontSize: "14px" }}
                  onClick={handleApprove}
                  disabled={approving || !schoolCodeInput}
                >
                  {approving ? "Creating School..." : "✓ Approve & Create School"}
                </button>
                <button
                  style={{ ...styles.actionButton, background: "#6c757d", color: "#fff", padding: "12px 24px", fontSize: "14px" }}
                  onClick={() => {
                    setShowApproveModal(false);
                    setSchoolCodeInput("");
                  }}
                  disabled={approving}
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      <ToastContainer />
    </>
  );
};

export default SchoolOnboardingDashboard;
