import React, { useEffect, useMemo, useState } from 'react';
import { jsPDF } from 'jspdf';
import { bulkCollectFees, fetchFeeSettings } from '../api';

interface BulkFeeCollectionModalProps {
    student: any;
    studentFees: any[];
    schoolId: string;
    academicYearId?: string;
    onClose: () => void;
    onSuccess: () => void;
}

type CollectionType = 'MONTH' | 'QUARTER' | 'YEAR';

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

function drawReceiptHeader(doc: jsPDF, schoolName: string, logoUrl?: string) {
    if (logoUrl) {
        try {
            doc.addImage(logoUrl, 20, 12, 24, 24);
        } catch (err) {
            console.warn('Unable to draw school logo on receipt', err);
        }
    }
    doc.setFontSize(20);
    doc.text(schoolName || 'MGPS Franchise School', 105, 20, { align: 'center' });
    doc.setFontSize(12);
    doc.text('Fee Receipt', 105, 30, { align: 'center' });
    doc.line(20, 38, 190, 38);
}

function previewAmountForFee(fee: any, collectionType: CollectionType) {
    const balance = fee.outstandingBalance ?? Math.max(0, (fee.totalDueTillDate || fee.amountDue) - fee.amountPaid - (fee.discountAmount || 0));
    if (fee.recurrenceType !== 'MONTHLY') {
        return balance > 0 ? balance : 0;
    }

    const paidThroughMonth = fee.paidThroughMonth || 0;
    const totalMonths = fee.totalMonthsInSession || 12;
    const remaining = Math.max(0, totalMonths - paidThroughMonth);
    if (remaining === 0) return 0;

    const monthsToCover = collectionType === 'MONTH' ? Math.min(1, remaining)
        : collectionType === 'QUARTER' ? Math.min(3, remaining)
            : remaining;
    return fee.amountDue * monthsToCover;
}

export default function BulkFeeCollectionModal({ student, studentFees, schoolId, academicYearId, onClose, onSuccess }: BulkFeeCollectionModalProps) {
    const [collectionType, setCollectionType] = useState<CollectionType>('MONTH');
    const [paymentMode, setPaymentMode] = useState<'CASH' | 'UPI' | 'ONLINE'>('CASH');
    const [transactionId, setTransactionId] = useState('');
    const [remarks, setRemarks] = useState('');
    const [yearlyDiscountPercent, setYearlyDiscountPercent] = useState<number>(0);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [result, setResult] = useState<any>(null);

    useEffect(() => {
        if (collectionType === 'YEAR') {
            fetchFeeSettings(schoolId)
                .then(settings => setYearlyDiscountPercent(settings.yearlyDiscountPercent || 0))
                .catch(() => setYearlyDiscountPercent(0));
        }
    }, [collectionType, schoolId]);

    const pendingFees = useMemo(() => studentFees.filter(sf => {
        const balance = sf.outstandingBalance ?? Math.max(0, (sf.totalDueTillDate || sf.amountDue) - sf.amountPaid - (sf.discountAmount || 0));
        return balance > 0;
    }), [studentFees]);

    const previewRows = useMemo(() => pendingFees.map(fee => {
        const beforeDiscount = previewAmountForFee(fee, collectionType);
        const discount = collectionType === 'YEAR' ? beforeDiscount * (yearlyDiscountPercent / 100) : 0;
        return {
            fee,
            beforeDiscount,
            discount,
            amountToCollect: Math.max(0, beforeDiscount - discount)
        };
    }).filter(row => row.beforeDiscount > 0), [pendingFees, collectionType, yearlyDiscountPercent]);

    const totalPending = pendingFees.reduce((sum, fee) => sum + (fee.outstandingBalance ?? 0), 0);
    const totalCollected = pendingFees.reduce((sum, fee) => sum + (fee.amountPaid ?? 0), 0);
    const totalToCollectNow = previewRows.reduce((sum, row) => sum + row.amountToCollect, 0);
    const totalDiscountNow = previewRows.reduce((sum, row) => sum + row.discount, 0);

    async function handleSubmit(e: React.FormEvent) {
        e.preventDefault();
        if (previewRows.length === 0) {
            setError('No pending fees to collect for the selected period.');
            return;
        }
        setIsSubmitting(true);
        setError(null);
        try {
            const payload = await bulkCollectFees({
                schoolId,
                studentId: student.studentId || student.id,
                academicYearId,
                collectionType,
                paymentMode,
                transactionId: paymentMode !== 'CASH' ? transactionId : undefined,
                remarks: remarks || undefined
            });
            setResult(payload);
            onSuccess();
        } catch (err) {
            setError(err instanceof Error ? err.message : 'Failed to collect fees');
        } finally {
            setIsSubmitting(false);
        }
    }

    function downloadConsolidatedReceipt() {
        if (!result) return;
        const payments: any[] = result.payments || [];
        const first = payments[0] || {};
        const doc = new jsPDF();
        drawReceiptHeader(doc, first.schoolName, first.schoolLogoUrl);

        doc.setFontSize(10);
        doc.text(`Receipt No: ${first.receiptNumber || '-'}`, 20, 48);
        doc.text(`Date: ${new Date().toLocaleDateString()}`, 150, 48);
        doc.text(`Student: ${student.firstName} ${student.lastName}`, 20, 58);
        doc.text(`Admission No: ${student.admissionNumber || '-'}`, 20, 63);
        doc.text(`Collection Type: ${result.collectionType}`, 20, 68);

        doc.setFillColor(245, 245, 245);
        doc.rect(20, 76, 170, 10, 'F');
        doc.setFont('helvetica', 'bold');
        doc.text('Fee Head', 24, 83);
        doc.text('Period', 100, 83);
        doc.text('Amount', 160, 83);
        doc.setFont('helvetica', 'normal');

        let y = 96;
        payments.forEach(p => {
            const period = p.monthFrom && p.monthTo ? `Month ${p.monthFrom}-${p.monthTo}` : '-';
            doc.text(p.feeCategoryName || 'Fee', 24, y);
            doc.text(period, 100, y);
            doc.text(`INR ${p.amountPaid.toLocaleString()}`, 160, y);
            y += 8;
        });

        doc.line(20, y, 190, y);
        y += 10;
        if (result.totalDiscountApplied > 0) {
            doc.text(`Discount Applied (${result.discountPercentApplied}%):`, 24, y);
            doc.text(`INR ${result.totalDiscountApplied.toLocaleString()}`, 160, y);
            y += 8;
        }
        doc.setFont('helvetica', 'bold');
        doc.text('Total Collected', 24, y);
        doc.text(`INR ${result.totalCollected.toLocaleString()}`, 160, y);
        doc.setFont('helvetica', 'normal');

        doc.text(`Payment Mode: ${paymentMode}`, 20, y + 16);
        doc.setFont('helvetica', 'italic');
        doc.text('Thank you for your payment.', 105, y + 36, { align: 'center' });

        saveReceiptPdf(doc, first.receiptNumber);
    }

    if (result) {
        return (
            <div className="modal-overlay" style={overlayStyle}>
                <div className="card" style={{ width: '480px', textAlign: 'center' }}>
                    <div style={{ fontSize: '3rem', color: 'var(--accent)', marginBottom: 16 }}>✅</div>
                    <h2>Fees Collected!</h2>
                    <p className="hint" style={{ marginBottom: 24 }}>
                        Collected ₹{result.totalCollected.toLocaleString()} across {result.payments.length} fee item(s)
                        {result.totalDiscountApplied > 0 && ` (₹${result.totalDiscountApplied.toLocaleString()} discount applied)`}.
                    </p>
                    <div className="actions" style={{ flexDirection: 'column', gap: 12 }}>
                        <button className="primary full" onClick={downloadConsolidatedReceipt}>Download Receipt</button>
                        <button className="secondary full" onClick={onClose}>Close</button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="modal-overlay" style={overlayStyle}>
            <form className="card" style={{ width: '640px', maxHeight: '85vh', overflowY: 'auto' }} onSubmit={handleSubmit}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
                    <h3>Collect All Fees — {student.firstName} {student.lastName}</h3>
                    <button type="button" onClick={onClose} style={{ background: 'none', border: 'none', color: 'var(--text)', fontSize: '1.5rem', cursor: 'pointer' }}>&times;</button>
                </div>

                <div style={{ background: 'rgba(255,255,255,0.03)', padding: 12, borderRadius: 8, marginBottom: 20, display: 'flex', justifyContent: 'space-between' }}>
                    <span className="hint">Already Collected: ₹{totalCollected.toLocaleString()}</span>
                    <span className="hint">Total Pending: ₹{totalPending.toLocaleString()}</span>
                </div>

                <label style={{ marginBottom: 16, display: 'block' }}>
                    Collection Type
                    <select value={collectionType} onChange={e => setCollectionType(e.target.value as CollectionType)}>
                        <option value="MONTH">Month</option>
                        <option value="QUARTER">Quarter</option>
                        <option value="YEAR">Full Year {yearlyDiscountPercent > 0 ? `(${yearlyDiscountPercent}% discount)` : ''}</option>
                    </select>
                </label>

                <div className="table-wrap" style={{ marginBottom: 16 }}>
                    <table className="module-table">
                        <thead>
                            <tr>
                                <th>Fee Head</th>
                                <th>Pending</th>
                                <th>Amount to Collect Now</th>
                            </tr>
                        </thead>
                        <tbody>
                            {previewRows.map(row => (
                                <tr key={row.fee.id}>
                                    <td>{row.fee.feeCategoryName}</td>
                                    <td>₹{(row.fee.outstandingBalance ?? 0).toLocaleString()}</td>
                                    <td>
                                        ₹{row.amountToCollect.toLocaleString()}
                                        {row.discount > 0 && (
                                            <div className="hint" style={{ color: 'var(--gold)' }}>-₹{row.discount.toLocaleString()} discount</div>
                                        )}
                                    </td>
                                </tr>
                            ))}
                            {previewRows.length === 0 && (
                                <tr><td colSpan={3} style={{ textAlign: 'center' }}>Nothing due for this period.</td></tr>
                            )}
                        </tbody>
                    </table>
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

                    <label>
                        Remarks (optional)
                        <input value={remarks} onChange={e => setRemarks(e.target.value)} placeholder="Notes for this collection" />
                    </label>
                </div>

                {error && <p className="error">{error}</p>}

                <div style={{ marginTop: 24, padding: 16, borderTop: '1px solid rgba(255,255,255,0.05)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    {totalDiscountNow > 0 && (
                        <span className="hint" style={{ color: 'var(--gold)' }}>Discount: -₹{totalDiscountNow.toLocaleString()}</span>
                    )}
                    <button type="submit" className="primary" disabled={isSubmitting || previewRows.length === 0} style={{ padding: '12px 32px', marginLeft: 'auto' }}>
                        {isSubmitting ? 'Processing...' : `Confirm ₹${totalToCollectNow.toLocaleString()}`}
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
