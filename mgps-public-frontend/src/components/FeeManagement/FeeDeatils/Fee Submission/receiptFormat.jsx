import Logo from '../../../../mgps_logo.png';

const ReceiptFormat = () => {
  return (
    <div style={{ fontFamily: "'Arial', sans-serif", padding: '20px', backgroundColor: '#f4f4f4' }}>
      <div style={{
        maxWidth: '800px',
        margin: 'auto',
        backgroundColor: '#fff',
        padding: '20px',
        borderRadius: '8px',
        boxShadow: '0 4px 8px rgba(0, 0, 0, 0.1)',
      }}>
        {/* Header */}
        <div style={{ display: 'flex', alignItems: 'center', marginBottom: '20px' }}>
          <div>
            <img src={Logo} alt="School Logo"
             style={{ width: '100px', height: '100px' }} />
          </div>
          <div style={{ textAlign: 'center', flex: 1 }}>
            <h4 style={{ margin: 0, fontSize: '1.5rem', color: '#333' }}>Mother's Goose</h4>
            <p style={{ margin: 0, fontSize: '0.9rem', color: '#555' }}>Bangalore, Karnataka</p>
          </div>
        </div>

        {/* Title */}
        <h5 style={{
          textAlign: 'center',
          marginBottom: '20px',
          fontSize: '1.2rem',
          color: '#4CAF50',
        }}>
          FEE RECEIPT (23-24 New)
        </h5>

        {/* Receipt Details */}
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
          <div>
            <p style={{ margin: '5px 0', fontSize: '0.95rem' }}><strong>TXN No:</strong> TR002</p>
            <p style={{ margin: '5px 0', fontSize: '0.95rem' }}><strong>Parent Name:</strong> Parent 3</p>
            <p style={{ margin: '5px 0', fontSize: '0.95rem' }}><strong>Admission No:</strong> -</p>
          </div>
          <div>
            <p style={{ margin: '5px 0', fontSize: '0.95rem' }}><strong>Receipt No:</strong> ENF/24-0035</p>
            <p style={{ margin: '5px 0', fontSize: '0.95rem' }}><strong>Student Name:</strong> SHIBAN MOHAMMED V T</p>
            <p style={{ margin: '5px 0', fontSize: '0.95rem' }}><strong>Class:</strong> Class - 3</p>
          </div>
        </div>

        {/* Fee Table */}
        <table style={{
          width: '100%',
          borderCollapse: 'collapse',
          marginBottom: '20px',
          fontSize: '0.95rem',
        }}>
          <thead>
            <tr style={{ backgroundColor: '#4CAF50', color: '#fff', textAlign: 'left' }}>
              <th style={{ padding: '10px', border: '1px solid #ddd' }}>Sl. no</th>
              <th style={{ padding: '10px', border: '1px solid #ddd' }}>Particulars</th>
              <th style={{ padding: '10px', border: '1px solid #ddd' }}>Due Month</th>
              <th style={{ padding: '10px', border: '1px solid #ddd' }}>Due Amount</th>
              <th style={{ padding: '10px', border: '1px solid #ddd' }}>Paid Amt.</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>1</td>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>ENRICHMENT FEE</td>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>Nov-23</td>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>0.00</td>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>8,500.00</td>
            </tr>
            <tr>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>2</td>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>Term 1</td>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>Nov-23</td>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>0.00</td>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>5,130.00</td>
            </tr>
            <tr>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>3</td>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>Term 3</td>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>Jan-24</td>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>0.00</td>
              <td style={{ padding: '10px', border: '1px solid #ddd' }}>9,510.00</td>
            </tr>
          </tbody>
        </table>

        {/* Payment Summary */}
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
          <div>
            <p style={{ margin: '5px 0', fontSize: '0.95rem' }}><strong>Paid Amount:</strong> 23,140.00</p>
            <p style={{ margin: '5px 0', fontSize: '0.95rem' }}><strong>Total Paid Fee:</strong> 28140</p>
            <p style={{ margin: '5px 0', fontSize: '0.95rem' }}>
              <strong>Amount in Words:</strong> Twenty Three Thousand One Hundred and Forty Rupees Only
            </p>
            <p style={{ margin: '5px 0', fontSize: '0.95rem' }}><strong>Payment Mode:</strong> Cash</p>
          </div>
          <div style={{ textAlign: 'right' }}>
            <p style={{ margin: '5px 0', fontSize: '0.95rem' }}><strong>Balance Due:</strong> 0.00</p>
          </div>
        </div>

        {/* Footer */}
        <div style={{ textAlign: 'right', marginTop: '40px' }}>
          <p style={{ fontSize: '0.95rem' }}><strong>Cashier / Manager</strong></p>
        </div>
      </div>
    </div>
  );
};

export default ReceiptFormat;
