import React from "react";
import './style.css';
import PropTypes from "prop-types";

const InputComponent = ({ 
  label, 
  type, 
  name, 
  value, 
  placeholder, 
  onChange, 
  size,
  error,
  required,
  icon,
  helperText,
  disabled = false
}) => {
  const formatLabel = (text) => {
    return text.replace(/([A-Z])/g, ' $1').trim();
  };

  return (
    <div className={`form-group col-${size}`}>
      <div className="form-label-wrapper">
        <label className="form-label" htmlFor={name}>
          {formatLabel(label)}
          {required && <span className="required-mark">*</span>}
          {!required && <span className="optional-mark">(optional)</span>}
        </label>
      </div>
      
      <div className={icon ? "input-icon-wrapper" : ""}>
        {icon && <span className="input-icon">{icon}</span>}
        
        {name === "gender" ? (
          <select
            className="form-control"
            id={name}
            name={name}
            value={value}
            onChange={onChange}
            required={required}
            disabled={disabled}
          >
            <option value="">Select Gender</option>
            <option value="Male">Male</option>
            <option value="Female">Female</option>
            <option value="Other">Other</option>
          </select>
        ) : name === "bloodGroup" ? (
          <select
            className="form-control"
            id={name}
            name={name}
            value={value}
            onChange={onChange}
            required={required}
            disabled={disabled}
          >
            <option value="">Select Blood Group</option>
            <option value="A+">A+</option>
            <option value="A-">A-</option>
            <option value="B+">B+</option>
            <option value="B-">B-</option>
            <option value="AB+">AB+</option>
            <option value="AB-">AB-</option>
            <option value="O+">O+</option>
            <option value="O-">O-</option>
          </select>
        ) : type === "textarea" ? (
          <textarea
            className="form-control"
            id={name}
            name={name}
            placeholder={placeholder || `Enter ${formatLabel(label)}`}
            value={value}
            onChange={onChange}
            required={required}
            rows={3}
            disabled={disabled}
          />
        ) : type === "file" ? (
          <div className="file-upload-wrapper">
            <label className="file-upload-label" htmlFor={name}>
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                <polyline points="17 8 12 3 7 8"/>
                <line x1="12" y1="3" x2="12" y2="15"/>
              </svg>
              Choose File
            </label>
            <input
              type="file"
              className="form-control"
              id={name}
              name={name}
              accept="image/*"
              onChange={onChange}
              required={required}
              disabled={disabled}
            />
            {value && <span className="file-name">{value.name || 'File selected'}</span>}
          </div>
        ) : (
          <input
            type={type}
            id={name}
            name={name}
            className="form-control"
            placeholder={placeholder || `Enter ${formatLabel(label)}`}
            value={value}
            onChange={onChange}
            required={required}
            disabled={disabled}
            minLength={type === "text" || type === "email" ? 2 : undefined}
          />
        )}
      </div>
      
      {error && <div className="form-error">{error}</div>}
      {helperText && !error && <div className="form-helper">{helperText}</div>}
    </div>
  );
};

InputComponent.defaultProps = {
  size: 6,
  required: true,
  error: null,
  icon: null,
  helperText: null,
  disabled: false
};

InputComponent.propTypes = {
  label: PropTypes.string.isRequired,
  type: PropTypes.string.isRequired,
  name: PropTypes.string.isRequired,
  value: PropTypes.string.isRequired,
  placeholder: PropTypes.string,
  onChange: PropTypes.func.isRequired,
  size: PropTypes.number,
  required: PropTypes.bool,
  error: PropTypes.string,
  icon: PropTypes.node,
  helperText: PropTypes.string,
  disabled: PropTypes.bool
};

export default InputComponent;
