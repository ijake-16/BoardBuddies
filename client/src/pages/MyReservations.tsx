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

const CheckIcon = ({ className }: { className?: string }) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" className={className}>
        <polyline points="20 6 9 17 4 12" />
    </svg>
);



export default function MyReservations({ onBack, onCrewClick }: MyReservationsProps) {
    const [selectedDay, setSelectedDay] = useState<number | null>(3); // Default to Today (3)

    // Mock Data based on screenshots
    const confirmedDays = [13, 14, 25, 26];
    const pendingDays = [27];
    const today = 3;

    const handleDayClick = (day: number) => {
        setSelectedDay(day);
    };

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
                    month="December"
                    year={2025}
                    startDayOfWeek={1}
                    totalDays={31}
                    expandable={false}
                    hideHeader={false}
                    onDayClick={handleDayClick}
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
                        const isToday = day === today;

                        let baseClasses = "flex justify-center text-sm font-bold transition-all duration-200 cursor-pointer";
                        let sizeClasses = isSelected ? "w-full h-full items-start pt-2" : "w-9 h-9 items-center";

                        if (isSelected) {
                            if (isConfirmed) {
                                return (
                                    <div className={`${baseClasses} ${sizeClasses} bg-[#1E3A8A] text-white rounded-[10px] shadow-sm`}>
                                        {day}
                                    </div>
                                );
                            }
                            if (isPending) {
                                return (
                                    <div className={`${baseClasses} ${sizeClasses} bg-[#1E3A8A]/60 text-white rounded-[10px] shadow-sm`}>
                                        {day}
                                    </div>
                                );
                            }
                            // Default Selected (Black Box)
                            return (
                                <div className={`${baseClasses} ${sizeClasses} bg-zinc-900 text-white rounded-[10px] shadow-lg scale-105`}>
                                    {day}
                                </div>
                            );
                        }

                        // Unselected States
                        if (isConfirmed) {
                            return (
                                <div className={`${baseClasses} ${sizeClasses} bg-[#1E3A8A] text-white rounded-full`}>
                                    {day}
                                </div>
                            );
                        }
                        if (isPending) {
                            return (
                                <div className={`${baseClasses} ${sizeClasses} bg-[#9CA3AF] text-white rounded-full`}>
                                    {day}
                                </div>
                            );
                        }
                        if (isToday) {
                            return (
                                <div className={`${baseClasses} ${sizeClasses} bg-[#F4F4F5] text-zinc-900 rounded-[10px]`}>
                                    {day}
                                </div>
                            );
                        }

                        // Default
                        return (
                            <div className={`${baseClasses} ${sizeClasses} text-zinc-500 hover:bg-zinc-100 rounded-full`}>
                                {day}
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
                                <div className="w-full bg-[#EDF2FF] rounded-[24px] p-5 flex items-center justify-between shadow-sm">
                                    <div className="flex items-center gap-2.5">
                                        <div className={`w-2.5 h-2.5 rounded-full ${isConfirmed ? 'bg-[#1E3A8A]' : 'bg-[#9CA3AF]'}`} />
                                        <span className="text-zinc-900 font-bold text-base">
                                            12/{String(selectedDay).padStart(2, '0')} 예약 {isConfirmed ? '확정' : '대기'}
                                        </span>
                                    </div>
                                    <Button
                                        variant="outline"
                                        className="bg-white border-zinc-200 text-zinc-500 hover:text-zinc-900 hover:bg-zinc-50 rounded-full px-4 h-9 text-sm font-medium shadow-sm transition-colors"
                                    >
                                        예약 취소
                                    </Button>
                                </div>
                            );
                        }

                        // Case: No Reservation (Default)
                        return (
                            <>
                                <div className="text-center mb-6">
                                    <p className="text-sm text-zinc-500 leading-relaxed">
                                        12/{selectedDay ? String(selectedDay).padStart(2, '0') : '--'} 예약내역이 없습니다.<br />
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
