import React from 'react';
import './style.css';
import kangarooImage from './kangaroo-bg.png'; // Adjust the path as needed

import { CONTACT_DETAILS,WHATSAPP_LINK } from "../../../constants/contactDetails";

const ThankYouPage = () => {
  return (
    <div className="thank-you-container">
      <div className="thank-you-content">
        <h1>Thank You!</h1>
        <h2>We appreciate your interest!</h2>
        <p className="congratulations">
          Your enquiry has been received. We will get back to you shortly.
        </p>
        <p className="toll-free">
          For any urgent queries, please call us at:
          <br />
          <span className="phone-number">&#128222; {CONTACT_DETAILS.phone1}, 

                    <a
                      href={WHATSAPP_LINK}
                      target="_blank"
                      rel="noopener noreferrer"
                      style={{ textDecoration: "none", color: "inherit" }}
                    >
                      {CONTACT_DETAILS.phone2} (WhatsApp)
                    </a>

          </span>
        </p>
        <p className="email">
        Or email us at: <a href={`mailto:${CONTACT_DETAILS.email}`}>{CONTACT_DETAILS.email}</a>
        </p>
      </div>
      <div className="thank-you-image">
        <img src={kangarooImage} alt="Kangaroo on a rocket pencil" />
      </div>
    </div>
  );
};

export default ThankYouPage;
