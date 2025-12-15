import { useState } from 'react';
import { Button } from '../components/Button';
import { ChevronLeftIcon } from 'lucide-react';
import { Calendar } from '../components/Calendar';

interface ReservationProps {
    onBack: () => void;
}

const CheckIcon = ({ className }: { className?: string }) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" className={className}>
        <polyline points="20 6 9 17 4 12" />
    </svg>
);

export default function Reservation({ onBack }: ReservationProps) {
    const [selectedDays, setSelectedDays] = useState<number[]>([]);
    // Mock existing reservations
    const [reservations, setReservations] = useState<number[]>([16, 20]);
    const [withdrawDay, setWithdrawDay] = useState<number | null>(null);

    // Mocking Today as Tuesday, Dec 14, 2025
    const availableStart = 15; // Wed
    const availableEnd = 18;   // Sat

    // Generate available days array
    const availableDays = Array.from(
        { length: availableEnd - availableStart + 1 },
        (_, i) => availableStart + i
    );

    const toggleDay = (day: number) => {
        // If it's an existing reservation, trigger withdrawal
        if (reservations.includes(day)) {
            setWithdrawDay(day);
            return;
        }

        // Otherwise, handle selection for new reservation
        if (!availableDays.includes(day)) return;

        setSelectedDays(prev =>
            prev.includes(day)
                ? prev.filter(d => d !== day)
                : [...prev, day]
        );
    };

    const confirmWithdraw = () => {
        if (withdrawDay !== null) {
            setReservations(prev => prev.filter(d => d !== withdrawDay));
            setWithdrawDay(null);
        }
    };

    return (
        <div className="flex-1 flex flex-col h-full overflow-hidden bg-white relative">
            {/* Header */}
            <header className="px-4 pt-12 pb-4 flex items-center justify-between z-10">
                <div className="w-10 flex justify-start">
                    <Button variant="ghost" onClick={onBack} className="-ml-2 gap-1 text-zinc-500 hover:text-zinc-900">
                        <ChevronLeftIcon className="w-6 h-6" />
                    </Button>
                </div>
                <h1 className="flex-1 text-center text-lg font-bold text-zinc-900">예약하기</h1>
                <div className="w-10" />
            </header>

            {/* Content */}
            <main className="flex-1 overflow-y-auto px-4 pb-[120px] flex flex-col items-center">


                <Calendar
                    className="mb-8 p-4" // Matches previous padding: p-4 vs p-6 default. Overriding p-6 with p-4 if needed? Tailwind might conflict. `p-6` is default. `p-4` was used. Let's stick to `p-6` default for consistency, or explicitly `p-4`. `p-4` is later in class string so should win.
                    month="December"
                    year={2025}
                    startDayOfWeek={1}
                    totalDays={31}
                    availableDays={availableDays}
                    selectedDays={selectedDays}
                    hideHeader={false}
                    renderDay={(day) => {
                        const isReserved = reservations.includes(day);
                        const isSelected = selectedDays.includes(day);
                        const isAvailable = availableDays.includes(day);

                        if (isReserved) {
                            return (
                                <button
                                    onClick={() => toggleDay(day)}
                                    className="w-8 h-8 rounded-full flex items-center justify-center text-sm font-bold text-white shadow-sm bg-[#162660] hover:bg-[#43A047] transition-colors relative"
                                >
                                    {day}
                                    <div className="absolute -top-1 -right-1 bg-white rounded-full p-0.5 shadow-sm">
                                        <CheckIcon className="w-3 h-3 text-[#162660]" />
                                    </div>
                                </button>
                            );
                        }

                        return (
                            <button
                                onClick={() => toggleDay(day)}
                                disabled={!isAvailable}
                                className={`
                                    w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium transition-all
                                    ${isSelected ? 'bg-[#F6C555] text-black shadow-sm' : ''}
                                    ${!isSelected && isAvailable ? 'text-black dark:text-white hover:bg-zinc-100 dark:hover:bg-zinc-800' : ''}
                                    ${!isSelected && !isAvailable ? 'text-zinc-300 dark:text-zinc-700 cursor-default' : ''}
                                `}
                            >
                                {day}
                            </button>
                        );
                    }}
                />

                {/* Apply Button */}
                <div className="w-full flex justify-center mt-auto">
                    <Button
                        disabled={selectedDays.length === 0}
                        className={`
                            w-full h-14 bg-[#162660] rounded-[20px] text-white text-lg font-bold
                            transition-all duration-200 shadow-md
                            ${selectedDays.length === 0 ? 'opacity-50 cursor-not-allowed' : 'opacity-100 hover:bg-[#7A8C9F] hover:scale-[1.02]'}
                        `}
                    >
                        신청하기
                    </Button>
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
                                variant="secondary"
                                onClick={() => setWithdrawDay(null)}
                                className="flex-1 bg-zinc-200 hover:bg-zinc-300 text-zinc-700 border-transparent"
                            >
                                돌아가기
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
