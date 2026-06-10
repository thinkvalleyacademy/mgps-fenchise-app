import React, { useState } from 'react';
import './style.css';

const HomeCard = ({ imageSrc, title, hoverText }) => {
    const [isHovered, setIsHovered] = useState(false);

    return (
      <div
        className="home-card"
        onMouseEnter={() => setIsHovered(true)}
        onMouseLeave={() => setIsHovered(false)}
      >
        {/* Image always visible */}
        <div className="home-card__content">
          <img src={imageSrc} alt={title} className="home-card__image" />
          {/* Hover text overlay */}
          {isHovered && (
            <div className="home-card__hover-overlay">
              <div className="home-card__hover-text">{hoverText}</div>
            </div>
          )}
        </div>
        {/* Title always visible at bottom */}
        <h3 className="home-card__title">{title}</h3>
      </div>
    );
  };

export default HomeCard;
