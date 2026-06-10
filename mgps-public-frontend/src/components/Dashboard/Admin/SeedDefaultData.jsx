import React, { useContext, useMemo, useState } from "react";
import AuthContext from "../../../context/student/AuthContext";
import { seedDefaultClasses } from "../../../apis/Admin/seedService";

const SeedDefaultData = () => {
  const { user } = useContext(AuthContext);
  const normalizedUser = user ? String(user).toLowerCase() : "";

  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");
  const [showRaw, setShowRaw] = useState(false);

  const schoolCode = useMemo(() => {
    const host = typeof window !== "undefined" ? window.location.hostname : "";
    const parts = host.split(".");
    if (parts.length > 2) return parts[0].toUpperCase();
    return process.env.REACT_APP_DEFAULT_SCHOOL_CODE || "MGPS";
  }, []);

  const runSeed = async () => {
    setLoading(true);
    setError("");
    setResult(null);
    try {
      const data = await seedDefaultClasses();
      setResult(data);
    } catch (e) {
      const msg =
        e?.response?.data?.message ||
        e?.response?.data?.data ||
        e?.message ||
        "Failed to seed default data";
      setError(String(msg));
    } finally {
      setLoading(false);
    }
  };

  if (normalizedUser !== "superadmin") {
    return (
      <div className="container-fluid p-4">
        <div className="alert alert-danger" role="alert">
          Access denied. This page is only available for superadmin.
        </div>
      </div>
    );
  }

  return (
    <div className="container-fluid p-4">
      <div className="d-flex align-items-center justify-content-between flex-wrap gap-2">
        <div>
          <h3 className="mb-1">Seed Default Data</h3>
          <div className="text-muted">
            Seeds classes, sections, and rooms for school code <b>{schoolCode}</b>.
          </div>
        </div>
        <div className="d-flex gap-2">
          {result ? (
            <button
              className="btn btn-outline-secondary"
              onClick={() => setShowRaw((v) => !v)}
              type="button"
            >
              {showRaw ? "Hide Raw" : "Show Raw"}
            </button>
          ) : null}
          <button
            className="btn btn-primary"
            onClick={runSeed}
            disabled={loading}
            title="Seeds Play Group, Nursery, LKG, UKG + Section A + Rooms"
            type="button"
          >
            {loading ? "Seeding..." : "Seed Default Classes"}
          </button>
        </div>
      </div>

      {error ? (
        <div className="alert alert-danger mt-3" role="alert">
          {error}
        </div>
      ) : null}

      {result ? (() => {
        const api = result;
        const payload = api?.data ?? api;
        const ok = Boolean(payload?.ok);
        const classes = payload?.classes ?? [];
        const sections = payload?.sections ?? [];
        const rooms = payload?.rooms ?? [];
        const message = api?.message || (ok ? "Seed completed" : "Seed failed");

        return (
          <div className="mt-3">
            <div className={`alert ${ok ? "alert-success" : "alert-warning"} d-flex justify-content-between align-items-center flex-wrap gap-2`} role="alert">
              <div>
                <div className="fw-bold">{message}</div>
                <div className="small">
                  School: <b>{payload?.schoolCode ?? schoolCode}</b>
                  {payload?.schoolId ? (
                    <>
                      {" "}• School ID: <b>{payload.schoolId}</b>
                    </>
                  ) : null}
                </div>
              </div>
              <div className="d-flex gap-2">
                <span className="badge bg-primary">Classes: {Array.isArray(classes) ? classes.length : 0}</span>
                <span className="badge bg-info text-dark">Sections: {Array.isArray(sections) ? sections.length : 0}</span>
                <span className="badge bg-secondary">Rooms: {Array.isArray(rooms) ? rooms.length : 0}</span>
              </div>
            </div>

            <div className="row g-3">
              <div className="col-12 col-lg-4">
                <div className="card h-100">
                  <div className="card-header d-flex justify-content-between align-items-center">
                    <span>Classes</span>
                    <span className="badge bg-primary">{Array.isArray(classes) ? classes.length : 0}</span>
                  </div>
                  <div className="card-body p-0">
                    <ul className="list-group list-group-flush">
                      {(Array.isArray(classes) && classes.length ? classes : ["—"]).map((c, idx) => (
                        <li key={idx} className="list-group-item">{c}</li>
                      ))}
                    </ul>
                  </div>
                </div>
              </div>

              <div className="col-12 col-lg-4">
                <div className="card h-100">
                  <div className="card-header d-flex justify-content-between align-items-center">
                    <span>Sections</span>
                    <span className="badge bg-info text-dark">{Array.isArray(sections) ? sections.length : 0}</span>
                  </div>
                  <div className="card-body p-0">
                    <ul className="list-group list-group-flush">
                      {(Array.isArray(sections) && sections.length ? sections : ["—"]).map((s, idx) => (
                        <li key={idx} className="list-group-item">{s}</li>
                      ))}
                    </ul>
                  </div>
                </div>
              </div>

              <div className="col-12 col-lg-4">
                <div className="card h-100">
                  <div className="card-header d-flex justify-content-between align-items-center">
                    <span>Rooms</span>
                    <span className="badge bg-secondary">{Array.isArray(rooms) ? rooms.length : 0}</span>
                  </div>
                  <div className="card-body p-0">
                    <ul className="list-group list-group-flush">
                      {(Array.isArray(rooms) && rooms.length ? rooms : ["—"]).map((r, idx) => (
                        <li key={idx} className="list-group-item">{r}</li>
                      ))}
                    </ul>
                  </div>
                </div>
              </div>
            </div>

            {showRaw ? (
              <div className="card mt-3">
                <div className="card-header d-flex justify-content-between align-items-center">
                  <span>Raw Response</span>
                  <button
                    type="button"
                    className="btn btn-sm btn-outline-secondary"
                    onClick={async () => {
                      try {
                        await navigator.clipboard.writeText(JSON.stringify(result, null, 2));
                      } catch {
                        // ignore clipboard failures (non-HTTPS, permissions, etc.)
                      }
                    }}
                  >
                    Copy
                  </button>
                </div>
                <div className="card-body">
                  <pre style={{ margin: 0, whiteSpace: "pre-wrap" }}>
                    {JSON.stringify(result, null, 2)}
                  </pre>
                </div>
              </div>
            ) : null}
          </div>
        );
      })() : null}
    </div>
  );
};

export default SeedDefaultData;
