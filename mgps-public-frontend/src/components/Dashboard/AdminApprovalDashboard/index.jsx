import React, { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { ToastContainer, toast } from "react-toastify";
import { Bounce } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import apiClient from "../../../apis/apiClient";

const AdminApprovalDashboard = () => {
  const navigate = useNavigate();
  const [pendingUsers, setPendingUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState(null);

  const notifyError = (msg) => {
    toast.error(msg, {
      position: "top-right",
      autoClose: 3000,
      hideProgressBar: true,
      closeOnClick: true,
      pauseOnHover: true,
      draggable: true,
      progress: undefined,
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
      progress: undefined,
      theme: "colored",
      transition: Bounce,
    });
  };

  const fetchPendingUsers = useCallback(async () => {
    try {
      const token = localStorage.getItem('authToken');
      if (!token) {
        notifyError("Please login as superadmin");
        navigate("/login");
        return;
      }

      const response = await apiClient.get('/admin/users/pending', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.data?.status === 200) {
        setPendingUsers(response.data.data || []);
      } else {
        notifyError(response.data?.message || "Failed to fetch pending users");
      }
    } catch (error) {
      console.error("Error fetching pending users:", error);
      if (error.response?.status === 401 || error.response?.status === 403) {
        notifyError("Unauthorized. Please login as superadmin");
        navigate("/login");
      } else {
        notifyError("Failed to fetch pending users");
      }
    } finally {
      setLoading(false);
    }
  }, [navigate]);

  useEffect(() => {
    fetchPendingUsers();
  }, [fetchPendingUsers]);

  const handleApprove = async (registrationNumber) => {
    try {
      setProcessing(registrationNumber);
      const token = localStorage.getItem('authToken');
      
      const response = await apiClient.post(`/admin/users/${registrationNumber}/approve`, {}, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.data?.status === 200) {
        notifySuccess("User approved successfully!");
        fetchPendingUsers();
      } else {
        notifyError(response.data?.message || "Failed to approve user");
      }
    } catch (error) {
      console.error("Error approving user:", error);
      notifyError(error.response?.data?.message || "Failed to approve user");
    } finally {
      setProcessing(null);
    }
  };

  const handleReject = async (registrationNumber) => {
    const reason = prompt("Enter rejection reason (optional):");
    
    try {
      setProcessing(registrationNumber);
      const token = localStorage.getItem('authToken');
      
      const response = await apiClient.post(`/admin/users/${registrationNumber}/reject`, null, {
        headers: {
          'Authorization': `Bearer ${token}`
        },
        params: reason ? { reason } : {}
      });

      if (response.data?.status === 200) {
        notifySuccess("User rejected");
        fetchPendingUsers();
      } else {
        notifyError(response.data?.message || "Failed to reject user");
      }
    } catch (error) {
      console.error("Error rejecting user:", error);
      notifyError(error.response?.data?.message || "Failed to reject user");
    } finally {
      setProcessing(null);
    }
  };

  const styles = {
    container: {
      padding: '20px',
      minHeight: '100%',
      background: '#f5f7fa',
    },
    card: {
      background: 'white',
      borderRadius: '15px',
      boxShadow: '0 4px 20px rgba(0,0,0,0.1)',
      maxWidth: '1200px',
      margin: '0 auto',
      overflow: 'hidden'
    },
    header: {
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
      color: 'white',
      padding: '30px',
      textAlign: 'center'
    },
    content: {
      padding: '30px'
    },
    table: {
      width: '100%',
      borderCollapse: 'collapse',
      marginTop: '20px'
    },
    th: {
      background: '#f8f9fa',
      padding: '15px',
      textAlign: 'left',
      fontWeight: '600',
      borderBottom: '2px solid #dee2e6'
    },
    td: {
      padding: '15px',
      borderBottom: '1px solid #dee2e6'
    },
    approveBtn: {
      padding: '8px 16px',
      background: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)',
      color: 'white',
      border: 'none',
      borderRadius: '8px',
      cursor: 'pointer',
      fontWeight: '600',
      marginRight: '8px'
    },
    rejectBtn: {
      padding: '8px 16px',
      background: 'linear-gradient(135deg, #eb3349 0%, #f45c43 100%)',
      color: 'white',
      border: 'none',
      borderRadius: '8px',
      cursor: 'pointer',
      fontWeight: '600'
    },
    emptyState: {
      textAlign: 'center',
      padding: '60px 20px',
      color: '#666'
    }
  };

  return (
    <>
      <div style={styles.container}>
        <div style={styles.card}>
          <div style={styles.header}>
            <h2 style={{ margin: 0, fontSize: '28px' }}>👥 Pending User Approvals</h2>
            <p style={{ margin: '10px 0 0', opacity: 0.9 }}>Review and approve new user registration requests</p>
          </div>

          <div style={styles.content}>
            {loading ? (
              <div style={{ textAlign: 'center', padding: '40px' }}>
                <p>Loading pending users...</p>
              </div>
            ) : pendingUsers.length === 0 ? (
              <div style={styles.emptyState}>
                <div style={{ fontSize: '64px', marginBottom: '20px' }}>✅</div>
                <h3>No Pending Approvals</h3>
                <p>All user accounts have been processed</p>
              </div>
            ) : (
              <div style={{ overflowX: 'auto' }}>
                <table style={styles.table}>
                  <thead>
                    <tr>
                      <th style={styles.th}>Name</th>
                      <th style={styles.th}>Username</th>
                      <th style={styles.th}>Email</th>
                      <th style={styles.th}>User Type</th>
                      <th style={styles.th}>Registration #</th>
                      <th style={styles.th}>Registered On</th>
                      <th style={styles.th}>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {pendingUsers.map((user, index) => (
                      <tr key={index}>
                        <td style={styles.td}>
                          {user.firstName} {user.lastName}
                        </td>
                        <td style={styles.td}>{user.username}</td>
                        <td style={styles.td}>{user.emailId}</td>
                        <td style={styles.td}>
                          <span style={{
                            padding: '4px 12px',
                            borderRadius: '12px',
                            background: '#e3f2fd',
                            color: '#1976d2',
                            fontSize: '12px',
                            fontWeight: '600'
                          }}>
                            {user.userType}
                          </span>
                        </td>
                        <td style={styles.td}>{user.registrationNumber}</td>
                        <td style={styles.td}>
                          {new Date(user.createdAt).toLocaleDateString()}
                        </td>
                        <td style={styles.td}>
                          <button
                            style={{
                              ...styles.approveBtn,
                              opacity: processing === user.registrationNumber ? 0.6 : 1,
                              cursor: processing === user.registrationNumber ? 'not-allowed' : 'pointer'
                            }}
                            onClick={() => handleApprove(user.registrationNumber)}
                            disabled={processing === user.registrationNumber}
                          >
                            {processing === user.registrationNumber ? 'Processing...' : '✓ Approve'}
                          </button>
                          <button
                            style={{
                              ...styles.rejectBtn,
                              opacity: processing === user.registrationNumber ? 0.6 : 1,
                              cursor: processing === user.registrationNumber ? 'not-allowed' : 'pointer'
                            }}
                            onClick={() => handleReject(user.registrationNumber)}
                            disabled={processing === user.registrationNumber}
                          >
                            ✗ Reject
                          </button>
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

      <ToastContainer />
    </>
  );
};

export default AdminApprovalDashboard;
