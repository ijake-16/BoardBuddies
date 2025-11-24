import { useState } from 'react';
import { Button } from './components/Button';
import { Calendar } from './components/Calendar';

interface MyReservationsProps {
    onBack: () => void;
}

const ChevronLeftIcon = ({ className }: { className?: string }) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
        <path d="m15 18-6-6 6-6" />
    </svg>
);

const CheckIcon = ({ className }: { className?: string }) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" className={className}>
        <polyline points="20 6 9 17 4 12" />
    </svg>
);

export default function MyReservations({ onBack }: MyReservationsProps) {
    // Mock reservations
    const [reservations, setReservations] = useState<number[]>([16, 20]);
    const [withdrawDay, setWithdrawDay] = useState<number | null>(null);

    const handleDayClick = (day: number) => {
        if (reservations.includes(day)) {
            setWithdrawDay(day);
        }
    };

    const confirmWithdraw = () => {
        if (withdrawDay !== null) {
            setReservations(prev => prev.filter(d => d !== withdrawDay));
            setWithdrawDay(null);
        }
    };

    return (
        <div className="flex-1 flex flex-col bg-zinc-50 dark:bg-zinc-900 h-full overflow-hidden relative">
            {/* Header */}
            <header className="px-6 pt-12 pb-4 flex items-center justify-between bg-white dark:bg-zinc-950 z-10 shadow-sm">
                <Button variant="ghost" onClick={onBack} className="-ml-2 gap-1 text-zinc-500 hover:text-zinc-900 dark:text-zinc-400 dark:hover:text-zinc-100">
                    <ChevronLeftIcon className="w-6 h-6" />
                    <span className="text-lg font-medium">Back</span>
                </Button>
                <h1 className="text-lg font-bold">내 예약</h1>
                <div className="w-12" /> {/* Spacer */}
            </header>

            <main className="flex-1 overflow-y-auto p-6 pb-[120px] flex flex-col">
                <div className="flex-1">
                    <Calendar
                        month="December"
                        year={2025}
                        startDayOfWeek={1}
                        totalDays={31}
                        renderDay={(day) => {
                            const isReserved = reservations.includes(day);
                            if (isReserved) {
                                return (
                                    <button
                                        onClick={() => handleDayClick(day)}
                                        className="w-10 h-10 rounded-full flex items-center justify-center text-sm font-bold text-white shadow-sm bg-[#4CAF50] hover:bg-[#43A047] transition-colors relative"
                                    >
                                        {day}
                                        <div className="absolute -top-1 -right-1 bg-white rounded-full p-0.5 shadow-sm">
                                            <CheckIcon className="w-3 h-3 text-[#4CAF50]" />
                                        </div>
                                    </button>
                                );
                            }
                            // Default rendering for non-reserved days (just the number, maybe greyed out if we want to show they aren't interactive)
                            return (
                                <div className="w-10 h-10 rounded-full flex items-center justify-center text-sm font-medium text-zinc-300 dark:text-zinc-700">
                                    {day}
                                </div>
                            );
                        }}
                    />
                </div>
            </main>

            {/* Withdrawal Modal */}
            {withdrawDay !== null && (
                <div className="absolute inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-6">
                    <div className="bg-white dark:bg-zinc-900 rounded-2xl p-6 w-full max-w-xs shadow-xl animate-in fade-in zoom-in duration-200">
                        <h3 className="text-xl font-bold mb-2">예약 취소</h3>
                        <p className="text-zinc-600 dark:text-zinc-400 mb-6">
                            <span className="font-bold text-zinc-900 dark:text-zinc-100">12월 {withdrawDay}일</span> 예약을 취소하시겠습니까?
                        </p>
                        <div className="flex gap-3">
                            <Button
                                variant="ghost"
                                onClick={() => setWithdrawDay(null)}
                                className="flex-1"
                            >
                                유지하기
                            </Button>
                            <Button
                                variant="primary"
                                onClick={confirmWithdraw}
                                className="flex-1 bg-red-500 hover:bg-red-600 border-red-500 hover:border-red-600 text-white"
                            >
                                취소하기
                            </Button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
