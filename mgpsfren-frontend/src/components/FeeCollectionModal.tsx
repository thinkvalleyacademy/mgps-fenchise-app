import React, { useState, useEffect } from 'react';
import { jsPDF } from 'jspdf';
import 'jspdf-autotable';
import { processPayment } from '../api';

interface FeeCollectionModalProps {
    student: any;
    fee: any;
    schoolId: string;
    onClose: () => void;
    onSuccess: () => void;
}

const MONTHS = [
    'April', 'May', 'June', 'July', 'August', 'September', 
    'October', 'November', 'December', 'January', 'February', 'March'
];

export default function FeeCollectionModal({ student, fee, schoolId, onClose, onSuccess }: FeeCollectionModalProps) {
    const isMonthly = fee.recurrenceType === 'MONTHLY';
    const totalDue = fee.totalDueTillDate || fee.amountDue;
    const balance = totalDue - fee.amountPaid - (fee.discountAmount || 0);

    const [paymentMode, setPaymentMode] = useState<'CASH' | 'UPI' | 'ONLINE'>('CASH');
    const [transactionId, setTransactionId] = useState('');
    const [tillMonth, setTillMonth] = useState<number>(0); 
    const [amountToPay, setAmountToPay] = useState<number>(balance);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [paymentResult, setPaymentResult] = useState<any>(null);

    useEffect(() => {
        if (isMonthly) {
            // Rough calculation of month index based on amount paid
            // In a production app, we'd fetch "paid_up_to" from backend
            const monthsPaid = Math.floor(fee.amountPaid / fee.amountDue);
            setTillMonth(Math.min(monthsPaid, 11));
            setAmountToPay(balance);
        } else {
            setAmountToPay(balance);
        }
    }, [fee, balance, isMonthly]);

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        if (amountToPay <= 0) return alert('Amount must be greater than 0');
        
        setIsSubmitting(true);
        try {
            const payload = {
                schoolId,
                studentFeeId: fee.id,
                amountPaid: amountToPay,
                paymentMode,
                transactionId: paymentMode !== 'CASH' ? transactionId : null,
                monthFrom: isMonthly ? 1 : null, // Simplified
                monthTo: isMonthly ? tillMonth + 1 : null,
                remarks: isMonthly ? `Paid through ${MONTHS[tillMonth]}` : 'One-time payment'
            };
            const result = await processPayment(payload);
            setPaymentResult(result);
            onSuccess();
        } catch (err) {
            alert(err instanceof Error ? err.message : 'Payment failed');
        } finally {
            setIsSubmitting(false);
        }
    }

    function generatePDF(payment: any) {
        const doc = new jsPDF() as any;
        
        doc.setFontSize(20);
        doc.text('MGPS Franchise School', 105, 20, { align: 'center' });
        doc.setFontSize(12);
        doc.text('Fee Receipt', 105, 30, { align: 'center' });
        doc.line(20, 35, 190, 35);
        
        doc.setFontSize(10);
        doc.text(`Receipt No: ${payment.receiptNumber}`, 20, 45);
        doc.text(`Date: ${new Date().toLocaleDateString()}`, 150, 45);
        doc.text(`Student Name: ${student.firstName} ${student.lastName}`, 20, 55);
        doc.text(`Admission No: ${student.admissionNumber}`, 20, 60);
        
        const tableData = [
            [fee.feeCategoryName, isMonthly ? `Till ${MONTHS[payment.monthTo - 1]}` : 'One-time', `INR ${payment.amountPaid.toLocaleString()}`],
            ['Total Collected', '', `INR ${payment.amountPaid.toLocaleString()}`]
        ];
        
        doc.autoTable({
            startY: 70,
            head: [['Description', 'Period', 'Amount']],
            body: tableData,
            theme: 'striped',
            headStyles: { fillColor: [212, 175, 55] }
        });
        
        const finalY = (doc as any).lastAutoTable.finalY || 100;
        doc.text(`Payment Mode: ${payment.paymentMode}`, 20, finalY + 15);
        if (payment.transactionId) doc.text(`Transaction ID: ${payment.transactionId}`, 20, finalY + 20);
        
        doc.setFont('helvetica', 'italic');
        doc.text('Thank you for your payment.', 105, finalY + 40, { align: 'center' });
        doc.save(`Receipt_${payment.receiptNumber}.pdf`);
    }

    if (paymentResult) {
        return (
            <div className="modal-overlay" style={overlayStyle}>
                <div className="card" style={{ width: '450px', textAlign: 'center' }}>
                    <div style={{ fontSize: '3rem', color: 'var(--accent)', marginBottom: 16 }}>✅</div>
                    <h2>Payment Successful!</h2>
                    <p className="hint" style={{ marginBottom: 24 }}>
                        Receipt <strong>{paymentResult.receiptNumber}</strong> generated for ₹{amountToPay.toLocaleString()}.
                    </p>
                    <div className="actions" style={{ flexDirection: 'column', gap: 12 }}>
                        <button className="primary full" onClick={() => generatePDF(paymentResult)}>Download PDF Receipt</button>
                        <button className="secondary full" onClick={onClose}>Close</button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="modal-overlay" style={overlayStyle}>
            <form className="card" style={{ width: '500px' }} onSubmit={handleSubmit}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
                    <h3>Collect Fee</h3>
                    <button type="button" onClick={onClose} style={{ background: 'none', border: 'none', color: 'var(--text)', fontSize: '1.5rem', cursor: 'pointer' }}>&times;</button>
                </div>

                <div style={{ background: 'rgba(255,255,255,0.03)', padding: 12, borderRadius: 8, marginBottom: 20 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                        <strong>{fee.feeCategoryName}</strong>
                        <span className="badge">{fee.recurrenceType}</span>
                    </div>
                    <p className="hint">Balance: ₹{balance.toLocaleString()}</p>
                </div>

                <div className="form-grid" style={{ gridTemplateColumns: '1fr' }}>
                    <label>
                        Payment Mode
                        <select value={paymentMode} onChange={e => setPaymentMode(e.target.value as any)}>
                            <option value="CASH">Offline (Cash)</option>
                            <option value="UPI">Online (UPI)</option>
                            <option value="ONLINE">Bank Transfer / Card</option>
                        </select>
                    </label>

                    {paymentMode !== 'CASH' && (
                        <label>
                            Transaction ID / Reference
                            <input value={transactionId} onChange={e => setTransactionId(e.target.value)} placeholder="Ref No" required />
                        </label>
                    )}

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                        <label>
                            Amount to Pay (Partial allowed)
                            <input 
                                type="number" 
                                value={amountToPay} 
                                onChange={e => setAmountToPay(parseFloat(e.target.value))} 
                                max={balance} 
                                required 
                            />
                        </label>

                        {isMonthly && (
                            <label>
                                Covering Up To
                                <select value={tillMonth} onChange={e => setTillMonth(parseInt(e.target.value))}>
                                    {MONTHS.map((m, i) => (
                                        <option key={m} value={i}>{m}</option>
                                    ))}
                                </select>
                            </label>
                        )}
                    </div>
                </div>

                <div style={{ marginTop: 24, padding: 16, borderTop: '1px solid rgba(255,255,255,0.05)', display: 'flex', justifyContent: 'flex-end' }}>
                    <button type="submit" className="primary" disabled={isSubmitting} style={{ padding: '12px 32px' }}>
                        {isSubmitting ? 'Processing...' : `Confirm ₹${amountToPay.toLocaleString()}`}
                    </button>
                </div>
            </form>
        </div>
    );
}

const overlayStyle: React.CSSProperties = {
    position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
    background: 'rgba(0,0,0,0.85)', display: 'flex', alignItems: 'center',
    justifyContent: 'center', zIndex: 1100
};
