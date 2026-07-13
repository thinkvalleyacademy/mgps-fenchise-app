import React, { useState, useEffect } from 'react';
import { jsPDF } from 'jspdf';
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

function saveReceiptPdf(doc: jsPDF, receiptNumber: string) {
    const fileName = `Receipt_${receiptNumber || Date.now()}.pdf`;
    try {
        doc.save(fileName);
    } catch (err) {
        const blobUrl = URL.createObjectURL(doc.output('blob'));
        const link = document.createElement('a');
        link.href = blobUrl;
        link.download = fileName;
        document.body.appendChild(link);
        link.click();
        link.remove();
        URL.revokeObjectURL(blobUrl);
    }
}

function drawReceiptHeader(doc: jsPDF, payment: any) {
    const schoolName = payment.schoolName || 'MGPS Franchise School';
    const logoUrl = payment.schoolLogoUrl;

    if (logoUrl) {
        try {
            doc.addImage(logoUrl, 20, 12, 24, 24);
        } catch (err) {
            console.warn('Unable to draw school logo on receipt', err);
        }
    }

    doc.setFontSize(20);
    doc.text(schoolName, 105, 20, { align: 'center' });
    doc.setFontSize(12);
    doc.text('Fee Receipt', 105, 30, { align: 'center' });
    doc.line(20, 38, 190, 38);
}

export default function FeeCollectionModal({ student, fee, schoolId, onClose, onSuccess }: FeeCollectionModalProps) {
    const isMonthly = fee.recurrenceType === 'MONTHLY';
    const totalDue = fee.totalDueTillDate || fee.amountDue;
    const balance = fee.outstandingBalance ?? Math.max(0, totalDue - fee.amountPaid - (fee.discountAmount || 0));
    const paidThroughMonth = isMonthly ? (fee.paidThroughMonth || 0) : 0;
    const totalMonthsInSession = isMonthly ? (fee.totalMonthsInSession || 12) : 1;

    const [paymentMode, setPaymentMode] = useState<'CASH' | 'UPI' | 'ONLINE'>('CASH');
    const [transactionId, setTransactionId] = useState('');
    const [tillMonth, setTillMonth] = useState<number>(paidThroughMonth); 
    const [amountToPay, setAmountToPay] = useState<number>(balance);
    const [paymentPlan, setPaymentPlan] = useState<'CUSTOM' | 'MONTH' | 'QUARTER' | 'YEAR'>('CUSTOM');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [paymentResult, setPaymentResult] = useState<any>(null);

    useEffect(() => {
        if (isMonthly) {
            const nextMonth = Math.min(paidThroughMonth + 1, totalMonthsInSession);
            setTillMonth(nextMonth);
            setPaymentPlan('CUSTOM');
            setAmountToPay(balance > 0 ? balance : fee.amountDue);
        } else {
            setAmountToPay(balance);
        }
    }, [fee, balance, isMonthly, paidThroughMonth, totalMonthsInSession]);

    useEffect(() => {
        if (!isMonthly || paymentPlan === 'CUSTOM') return;
        const monthsRemaining = Math.max(0, totalMonthsInSession - paidThroughMonth);
        const monthsToPay = paymentPlan === 'MONTH' ? 1 : paymentPlan === 'QUARTER' ? Math.min(3, monthsRemaining) : monthsRemaining;
        const calculatedTillMonth = Math.min(totalMonthsInSession, paidThroughMonth + monthsToPay);
        setTillMonth(calculatedTillMonth);
    }, [fee.amountDue, isMonthly, paidThroughMonth, paymentPlan, totalMonthsInSession]);

    useEffect(() => {
        if (!isMonthly || paymentPlan === 'CUSTOM') return;
        const monthsToPay = Math.max(1, tillMonth - paidThroughMonth);
        setAmountToPay(fee.amountDue * monthsToPay);
    }, [fee.amountDue, isMonthly, paidThroughMonth, paymentPlan, tillMonth]);

    const selectedMonthRange = isMonthly && paymentPlan !== 'CUSTOM' && tillMonth > paidThroughMonth;

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
                monthFrom: selectedMonthRange ? paidThroughMonth + 1 : null,
                monthTo: selectedMonthRange ? tillMonth : null,
                remarks: isMonthly
                    ? selectedMonthRange ? `Paid through ${MONTHS[tillMonth - 1] || `month ${tillMonth}`}` : 'Monthly fee payment'
                    : 'One-time payment'
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
        const doc = new jsPDF();
        const period = isMonthly
            ? payment.monthTo ? `Till ${MONTHS[payment.monthTo - 1]}` : 'Monthly payment'
            : 'One-time';

        drawReceiptHeader(doc, payment);
        
        doc.setFontSize(10);
        doc.text(`Receipt No: ${payment.receiptNumber}`, 20, 48);
        doc.text(`Date: ${new Date().toLocaleDateString()}`, 150, 48);
        doc.text(`Student Name: ${payment.studentName || `${student.firstName} ${student.lastName}`}`, 20, 58);
        doc.text(`Admission No: ${payment.admissionNumber || student.admissionNumber}`, 20, 63);

        doc.setFillColor(245, 245, 245);
        doc.rect(20, 73, 170, 10, 'F');
        doc.setFont('helvetica', 'bold');
        doc.text('Description', 24, 80);
        doc.text('Period', 94, 80);
        doc.text('Amount', 150, 80);
        doc.setFont('helvetica', 'normal');
        doc.text(payment.feeCategoryName || fee.feeCategoryName || 'Fee', 24, 93);
        doc.text(period, 94, 93);
        doc.text(`INR ${payment.amountPaid.toLocaleString()}`, 150, 93);
        doc.line(20, 99, 190, 99);
        doc.setFont('helvetica', 'bold');
        doc.text('Total Collected', 24, 109);
        doc.text(`INR ${payment.amountPaid.toLocaleString()}`, 150, 109);
        doc.setFont('helvetica', 'normal');

        doc.text(`Payment Mode: ${payment.paymentMode}`, 20, 128);
        if (payment.transactionId) doc.text(`Transaction ID: ${payment.transactionId}`, 20, 135);
        
        doc.setFont('helvetica', 'italic');
        doc.text('Thank you for your payment.', 105, 158, { align: 'center' });
        saveReceiptPdf(doc, payment.receiptNumber);
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
                    {isMonthly && (
                        <p className="hint">Paid through {paidThroughMonth}/{totalMonthsInSession} months</p>
                    )}
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

                    {isMonthly && (
                        <label>
                            Collection Type
                            <select value={paymentPlan} onChange={e => setPaymentPlan(e.target.value as any)}>
                                <option value="CUSTOM">Custom / random amount</option>
                                <option value="MONTH">Next month</option>
                                <option value="QUARTER">Next quarter</option>
                                <option value="YEAR">Remaining full year</option>
                            </select>
                        </label>
                    )}

                    <div style={{ display: 'grid', gridTemplateColumns: isMonthly && paymentPlan !== 'CUSTOM' ? '1fr 1fr' : '1fr', gap: 12 }}>
                        <label>
                            Amount to Pay (Partial allowed)
                            <input 
                                type="number" 
                                value={amountToPay} 
                                onChange={e => setAmountToPay(parseFloat(e.target.value))} 
                                min="1"
                                max={!isMonthly && balance > 0 ? balance : undefined}
                                readOnly={isMonthly && paymentPlan !== 'CUSTOM'}
                                required 
                            />
                        </label>

                        {isMonthly && paymentPlan !== 'CUSTOM' && (
                            <label>
                                Pay Through
                                <select value={tillMonth} onChange={e => setTillMonth(parseInt(e.target.value))}>
                                    {MONTHS.slice(0, totalMonthsInSession).map((m, i) => (
                                        <option key={m} value={i + 1} disabled={i + 1 <= paidThroughMonth}>{m}</option>
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
