import { useState, useEffect } from 'react';
import { Button } from '../components/Button';
import { ChevronLeftIcon } from 'lucide-react';
import { Calendar } from '../components/Calendar';
import { createReservation, cancelReservation } from '../services/crew';
import { getMyReservations, getUserInfo } from '../services/user';
import { MyReservation } from '../types/api';


interface ReservationProps {
    onBack: () => void;
}

const CheckIcon = ({ className }: { className?: string }) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" className={className}>
        <polyline points="20 6 9 17 4 12" />
    </svg>
);

export default function Reservation({ onBack }: ReservationProps) {
    const todayDate = new Date();

    // Dynamic View Date State
    const [viewDate, setViewDate] = useState(new Date());
    const currentYear = viewDate.getFullYear();
    const currentMonthIndex = viewDate.getMonth(); // 0-11

    // Check if "Today" is in the currently viewed month/year to highlight it
    const isCurrentMonthView = todayDate.getFullYear() === currentYear && todayDate.getMonth() === currentMonthIndex;
    const todayDay = todayDate.getDate();

    const monthNames = [
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    ];
    const currentMonthName = monthNames[currentMonthIndex];

    // Calculate days in current month
    const daysInMonth = new Date(currentYear, currentMonthIndex + 1, 0).getDate();

    // Calculate start day of week (0=Sun, 1=Mon, etc.)
    const firstDayOfMonth = new Date(currentYear, currentMonthIndex, 1).getDay();

    // Selected days for NEW reservation
    const [selectedDays, setSelectedDays] = useState<number[]>([]);

    // Existing Reservations (Fetched from API)
    const [myReservations, setMyReservations] = useState<MyReservation[]>([]);

    // User's Crew ID
    const [crewId, setCrewId] = useState<number | null>(null);

    // Fetch Reservations on Mount
    const fetchReservations = async () => {
        try {
            const data = await getMyReservations();
            setMyReservations(data);
        } catch (error) {
            console.error("Failed to fetch my reservations:", error);
        }
    };

    // Fetch User Info to get Crew ID
    const fetchUserCrewId = async () => {
        try {
            const userData = await getUserInfo();
            if (userData.crew && userData.crew.crewId) {
                setCrewId(userData.crew.crewId);
            }
        } catch (error) {
            console.error("Failed to fetch user info:", error);
        }
    };

    useEffect(() => {
        fetchUserCrewId();
        fetchReservations();
    }, []);

    // Derived state: reserved days for the current month view
    const reservedDays = myReservations
        .filter(r => {
            const d = new Date(r.date);
            // Consider "confirmed" status or all? User implied we can cancel them, so list all active ones.
            // Assuming 'confirmed' or 'pending' or 'created' are valid to cancel.
            return d.getFullYear() === currentYear && d.getMonth() === currentMonthIndex && (r.status === 'confirmed' || r.status === 'created' || r.status === 'pending');
        })
        .map(r => new Date(r.date).getDate());


    const [withdrawDay, setWithdrawDay] = useState<number | null>(null);

    // Mock Availability: All days in future are available
    // A simple check: day >= today
    const isDayAvailable = (day: number) => {
        if (!isCurrentMonthView) return true; // Future/Past months logic (simplified)
        return day >= todayDay;
    };

    const toggleDay = (day: number) => {
        // If it's an existing reservation, trigger withdrawal
        if (reservedDays.includes(day)) {
            setWithdrawDay(day);
            return;
        }

        // Selection logic for NEW reservation
        if (!isDayAvailable(day)) return;

        setSelectedDays(prev =>
            prev.includes(day)
                ? prev.filter(d => d !== day)
                : [...prev, day]
        );
    };

    const confirmWithdraw = async () => {
        if (withdrawDay !== null && crewId) {
            try {
                const dateStr = `${currentYear}-${String(currentMonthIndex + 1).padStart(2, '0')}-${String(withdrawDay).padStart(2, '0')}`;
                await cancelReservation(crewId, [dateStr]);
                alert("예약이 취소되었습니다.");

                // Refresh list
                await fetchReservations();
            } catch (error) {
                console.error("Cancellation failed:", error);
                alert("예약 취소에 실패했습니다.");
            } finally {
                setWithdrawDay(null);
            }
        }
    };

    // Navigation Bounds
    const minDate = new Date(2025, 9, 1); // October 2025
    const maxDate = new Date(2026, 4, 31); // May 2026

    const handlePrevMonth = () => {
        const newDate = new Date(currentYear, currentMonthIndex - 1, 1);
        if (newDate >= minDate) {
            setViewDate(newDate);
            setSelectedDays([]); // Reset selection on month change
        }
    };

    const handleNextMonth = () => {
        const newDate = new Date(currentYear, currentMonthIndex + 1, 1);
        if (newDate <= maxDate) {
            setViewDate(newDate);
            setSelectedDays([]); // Reset selection
        }
    };

    const canGoPrev = new Date(currentYear, currentMonthIndex - 1, 1) >= minDate;
    const canGoNext = new Date(currentYear, currentMonthIndex + 1, 1) <= maxDate;

    // Submit Reservation
    const handleSubmit = async () => {
        if (selectedDays.length === 0) return;

        if (!crewId) {
            alert("크루 정보를 불러오는 중입니다. 잠시 후 다시 시도해주세요.");
            return;
        }

        // Format dates
        const formattedDates = selectedDays.map(day => {
            return `${currentYear}-${String(currentMonthIndex + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
        });

        try {
            await createReservation(crewId, formattedDates);
            alert("예약 신청이 완료되었습니다."); // Simple feedback
            setSelectedDays([]); // Clear selection
            // Refresh list so the new reservation appears as "reserved" immediately
            await fetchReservations();
            // Optional: onBack(); 
        } catch (error) {
            console.error("Reservation creation failed:", error);
            alert("예약 신청에 실패했습니다.");
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
                    className="mb-8 p-4"
                    month={currentMonthName}
                    year={currentYear}
                    startDayOfWeek={firstDayOfMonth}
                    totalDays={daysInMonth}
                    // availableDays={availableDays} // Removed static availableDays
                    selectedDays={selectedDays}
                    hideHeader={false}
                    onPrevMonth={canGoPrev ? handlePrevMonth : undefined}
                    onNextMonth={canGoNext ? handleNextMonth : undefined}
                    renderDay={(day) => {
                        const isReserved = reservedDays.includes(day);
                        const isSelected = selectedDays.includes(day);
                        const isAvailable = isDayAvailable(day);
                        const isToday = day === todayDay && isCurrentMonthView;

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

                        // Base styles
                        let buttonClasses = "w-8 h-8 rounded-full flex items-center justify-center text-sm font-medium transition-all relative";

                        // Today marker (if not selected)
                        if (isToday && !isSelected) {
                            buttonClasses += " bg-[#F4F4F5] text-zinc-900 font-bold";
                        } else if (isSelected) {
                            buttonClasses += " bg-[#F6C555] text-black shadow-sm font-bold scale-110";
                        } else if (isAvailable) {
                            buttonClasses += " text-black dark:text-white hover:bg-zinc-100 dark:hover:bg-zinc-800";
                        } else {
                            buttonClasses += " text-zinc-300 dark:text-zinc-700 cursor-default";
                        }

                        return (
                            <button
                                onClick={() => toggleDay(day)}
                                disabled={!isAvailable}
                                className={buttonClasses}
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
                        onClick={handleSubmit}
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
                            <span className="font-bold text-zinc-900 dark:text-zinc-100">{currentMonthIndex + 1}월 {withdrawDay}일</span> 예약을 취소하시겠습니까?
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
