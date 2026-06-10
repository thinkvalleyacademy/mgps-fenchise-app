import React, { useEffect, useRef } from "react";
import QuickRegistrationForm from "./QuickRegistrationForm";
import { CONTACT_DETAILS } from "../../../constants/contactDetails";

const AutoOpenModal = () => {
  const modalRef = useRef(null);

  const whatsappLink1 = `https://wa.me/${String(CONTACT_DETAILS.phone2).replace(/\D/g, "")}`;
  const whatsappLink2 = `https://wa.me/${String(CONTACT_DETAILS.phone1).replace(/\D/g, "")}`;

  useEffect(() => {
    // Function to open modal
    const openModal = () => {
      const modalElement = document.getElementById("myModal");
      if (modalElement && window.bootstrap && window.bootstrap.Modal) {
        try {
          if (!modalRef.current) {
            modalRef.current = new window.bootstrap.Modal(modalElement, {
              keyboard: true,
              backdrop: true, // Enable backdrop click close
            });
          }
          modalRef.current.show();
        } catch (error) {
          console.error("Error initializing modal:", error);
        }
      }
    };

    // Listen for custom event to open modal
    window.addEventListener('openEnquiryModal', openModal);

    // Initial modal show (for homepage load)
    const timer = setTimeout(() => {
      // Only show on homepage, not if triggered by event
      const modalElement = document.getElementById("myModal");
      if (modalElement && window.bootstrap && window.bootstrap.Modal && !modalRef.current) {
        openModal();
      }
    }, 200);

    // Cleanup function
    return () => {
      clearTimeout(timer);
      window.removeEventListener('openEnquiryModal', openModal);
      if (modalRef.current) {
        try {
          modalRef.current.hide();
          modalRef.current.dispose();
        } catch (error) {
          console.error("Error disposing modal:", error);
        }
      }
    };
  }, []);

  const closePopup = () => {
    if (modalRef.current) {
      try {
        modalRef.current.hide();
      } catch (error) {
        console.error("Error closing modal:", error);
      }
    }
  };

  return (
    <div>
      {/* Bootstrap Modal */}
      <div
        className="modal fade"
        id="myModal"
        tabIndex="-1"
        aria-labelledby="myModalLabel"
        aria-hidden="true"
        data-bs-backdrop="static"
        data-bs-keyboard="false"
      >
        <div className="modal-dialog modal-dialog-centered">
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title text-center w-100 fw-bold mb-1" id="myModalLabel">
                Enquiry Form
              </h5>
              <button
                type="button"
                className="btn-close"
                aria-label="Close"
                onClick={closePopup}
              ></button>
            </div>
            <div className="modal-body">
              {/* WhatsApp contact option (top) */}
              <div className="mb-3 text-center">
                <p className="mb-1">Contact us on WhatsApp</p>
                <div className="d-flex flex-column align-items-center gap-2">
                  <a
                    href={whatsappLink1}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="btn btn-success"
                    title="Chat on WhatsApp"
                  >
                    <i className="fab fa-whatsapp me-2"></i> {CONTACT_DETAILS.phone2}
                  </a>
                  <div className="text-muted fw-semibold">OR</div>
                  <a
                    href={whatsappLink2}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="btn btn-success"
                    title="Chat on WhatsApp"
                  >
                    <i className="fab fa-whatsapp me-2"></i> {CONTACT_DETAILS.phone1}
                  </a>
                </div>
              </div>

              {/* Quick Registration Form */}
              <QuickRegistrationForm />
            </div>
            <div className="modal-footer">
              <button
                type="button"
                className="btn btn-secondary"
                onClick={closePopup}
              >
                Close
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AutoOpenModal;
