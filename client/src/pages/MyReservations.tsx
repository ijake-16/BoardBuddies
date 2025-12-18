import { useState } from 'react';
import { Button } from '../components/Button';
import { Calendar } from '../components/Calendar';


interface MyReservationsProps {
    onBack: () => void;
    onCrewClick?: () => void;
}

const ChevronLeftIcon = ({ className }: { className?: string }) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
        <path d="m15 18-6-6 6-6" />
    </svg>
);




export default function MyReservations({ onBack, onCrewClick }: MyReservationsProps) {
    const todayDate = new Date();

    // Default to the current date, but clamp to the range if outside? 
    // For now, I'll stick to real current date as the starting point.
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

    const [selectedDay, setSelectedDay] = useState<number | null>(isCurrentMonthView ? todayDay : null);
    const [isLessonApplied, setIsLessonApplied] = useState(false);

    // Mock Data based on screenshots - kept static for now as per instructions (only fix "Today" and calendar grid)
    // In a real app these would likely come from props or API based on the current month
    const confirmedDays = [13, 14, 25, 26];
    const pendingDays = [27];

    const handleDayClick = (day: number) => {
        setSelectedDay(day);
        // Reset toggle when changing days for demo purposes
        setIsLessonApplied(false);
    };

    // Navigation Bounds
    const minDate = new Date(2025, 9, 1); // October 2025 (Month is 0-indexed: 9=Oct)
    const maxDate = new Date(2026, 4, 31); // May 2026 (Month is 0-indexed: 4=May)

    const handlePrevMonth = () => {
        const newDate = new Date(currentYear, currentMonthIndex - 1, 1);
        if (newDate >= minDate) {
            setViewDate(newDate);
            setSelectedDay(null); // Deselect when changing months
        }
    };

    const handleNextMonth = () => {
        const newDate = new Date(currentYear, currentMonthIndex + 1, 1);
        // We only care about the month/year, so check if the first of the next month is <= maxDate
        if (newDate <= maxDate) {
            setViewDate(newDate);
            setSelectedDay(null); // Deselect when changing months
        }
    };

    // Determine if buttons should be enabled
    const canGoPrev = new Date(currentYear, currentMonthIndex - 1, 1) >= minDate;
    const canGoNext = new Date(currentYear, currentMonthIndex + 1, 1) <= maxDate;

    return (
        <div className="flex-1 flex flex-col h-full overflow-hidden bg-white relative">
            {/* Header */}
            <header className="px-4 pt-12 pb-4 flex items-center justify-between z-10">
                <div className="w-20 flex justify-start"> {/* Fixed width wrapper */}
                    <Button variant="ghost" onClick={onBack} className="-ml-2 gap-1 text-zinc-900 hover:bg-transparent">
                        <ChevronLeftIcon className="w-6 h-6" />
                    </Button>
                </div>
                <h1 className="flex-1 text-center text-lg font-bold text-zinc-900">
                    나의 달력
                </h1>
                <div className="w-20 flex justify-end"> {/* Fixed width wrapper */}
                    <Button
                        variant="ghost"
                        onClick={onCrewClick}
                        className="text-xs text-zinc-400 hover:text-zinc-600 font-medium px-0"
                    >
                        그루 달력 <ChevronLeftIcon className="w-4 h-4 rotate-180" />
                    </Button>
                </div>
            </header>

            <main className="flex-1 overflow-y-auto px-4 pb-[120px] flex flex-col items-center">

                <Calendar
                    className="mb-8"
                    month={currentMonthName}
                    year={currentYear}
                    startDayOfWeek={firstDayOfMonth}
                    totalDays={daysInMonth}
                    expandable={false}
                    hideHeader={false}
                    onDayClick={handleDayClick}
                    onPrevMonth={canGoPrev ? handlePrevMonth : undefined}
                    onNextMonth={canGoNext ? handleNextMonth : undefined}
                    headerRight={
                        <div className="bg-[#EDF2FF] px-4 py-2 rounded-full flex gap-3 items-center shadow-sm">
                            <span className="text-xs font-bold text-zinc-900">시즌방 이용 횟수 :</span>
                            <span className="text-xs font-bold text-zinc-900">13박</span>
                        </div>
                    }
                    renderDay={(day) => {
                        const isSelected = selectedDay === day;
                        const isConfirmed = confirmedDays.includes(day);
                        const isPending = pendingDays.includes(day);
                        const isToday = day === todayDay && isCurrentMonthView;

                        // Base Container Classes
                        let containerClasses = "w-full h-full flex flex-col items-center justify-start pt-1.5 transition-all duration-200 cursor-pointer text-sm font-bold rounded-[10px]";

                        if (isSelected) {
                            containerClasses += " bg-[#333333] text-white shadow-lg scale-105";
                        } else if (isToday) {
                            containerClasses += " bg-[#F4F4F5] text-zinc-900";
                        } else {
                            containerClasses += " text-zinc-500 hover:bg-zinc-100/50";
                        }

                        // Content (Number or Circle)
                        let numberElement = <span>{day}</span>;

                        if (isConfirmed || isPending) {
                            const bg = isConfirmed ? 'bg-[#1E3A8A]' : 'bg-[#9CA3AF]';
                            const textColor = 'text-white';
                            // Using w-7 h-7 -mt-1 to look exactly like ReservationStats overlay style
                            numberElement = (
                                <div className={`w-7 h-7 -mt-1 rounded-full ${bg} ${textColor} flex items-center justify-center text-xs shadow-sm`}>
                                    {day}
                                </div>
                            );
                        }

                        return (
                            <div className={containerClasses}>
                                {numberElement}
                            </div>
                        );
                    }}
                />

                {/* Legend */}
                <div className="w-full flex justify-end gap-3 mb-10">
                    <div className="flex items-center gap-1.5">
                        <div className="w-2 h-2 rounded-full bg-[#1E3A8A]" />
                        <span className="text-[10px] text-zinc-500 font-medium">확정</span>
                    </div>
                    <div className="flex items-center gap-1.5">
                        <div className="w-2 h-2 rounded-full bg-[#9CA3AF]" />
                        <span className="text-[10px] text-zinc-500 font-medium">대기</span>
                    </div>
                </div>

                {/* Bottom Action Section */}
                <div className="w-full mt-auto">
                    {(() => {
                        const isConfirmed = selectedDay && confirmedDays.includes(selectedDay);
                        const isPending = selectedDay && pendingDays.includes(selectedDay);

                        // Case: Selected Reservation (Confirmed/Pending)
                        if (isConfirmed || isPending) {
                            return (
                                <div className="w-full flex flex-col gap-3">
                                    {isConfirmed && (
                                        <div className="flex items-center justify-end gap-2 px-1">
                                            <span className="text-xs font-bold text-zinc-900">강습 신청하기</span>
                                            <button
                                                className="w-10 h-6 bg-zinc-200 rounded-full relative transition-colors duration-200 ease-in-out data-[checked=true]:bg-[#1E3A8A]"
                                                data-checked={isLessonApplied}
                                                onClick={() => setIsLessonApplied(!isLessonApplied)}
                                            >
                                                <span className="absolute top-1 left-1 bg-white w-4 h-4 rounded-full transition-transform duration-200 ease-in-out data-[checked=true]:translate-x-4"
                                                    data-checked={isLessonApplied}
                                                />
                                            </button>
                                        </div>
                                    )}
                                    <div className="w-full bg-[#EDF2FF] rounded-[24px] p-5 flex items-center justify-between shadow-sm">
                                        <div className="flex items-center gap-2.5">
                                            <div className={`w-2.5 h-2.5 rounded-full ${isConfirmed ? 'bg-[#1E3A8A]' : 'bg-[#9CA3AF]'}`} />
                                            <span className="text-zinc-900 font-bold text-base">
                                                {currentMonthIndex + 1}/{String(selectedDay).padStart(2, '0')} 예약 {isConfirmed ? '확정' : '대기'}
                                            </span>
                                        </div>
                                        <Button
                                            variant="outline"
                                            className="bg-white border-zinc-200 text-zinc-500 hover:text-zinc-900 hover:bg-zinc-50 rounded-full px-4 h-9 text-sm font-medium shadow-sm transition-colors"
                                        >
                                            예약 취소
                                        </Button>
                                    </div>
                                </div>
                            );
                        }

                        // Case: No Reservation (Default)
                        return (
                            <>
                                <div className="text-center mb-6">
                                    <p className="text-sm text-zinc-500 leading-relaxed">
                                        {currentMonthIndex + 1}/{selectedDay ? String(selectedDay).padStart(2, '0') : '--'} 예약내역이 없습니다.<br />
                                        예약하시겠습니까?
                                    </p>
                                </div>
                                <Button
                                    className="w-full h-14 bg-[#162660] hover:bg-[#1E3A8A] rounded-[20px] text-white text-lg font-bold shadow-md transition-all active:scale-[0.98]"
                                >
                                    예약하기
                                </Button>
                            </>
                        );
                    })()}
                </div>

            </main>
        </div>
    );
}
