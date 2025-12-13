import { useState } from 'react';
import { ChevronLeftIcon, ChevronRightIcon, Smile } from 'lucide-react';
import { Button } from '../components/Button';
import { Calendar } from '../components/Calendar';


interface ReservationStatsProps {
    onBack: () => void;
    onMyCalendarClick?: () => void;
    initialView?: 'crew' | 'my';
}



export default function ReservationStats({ onBack, onMyCalendarClick, initialView = 'crew' }: ReservationStatsProps) {
    const [selectedDay, setSelectedDay] = useState<number>(5);
    const [isExpanded, setIsExpanded] = useState(false);
    const [showMySchedule, setShowMySchedule] = useState(false);
    const today = 3;

    // Mock Reservation Data
    const confirmedDays = [13, 14, 25, 26];
    const pendingDays = [27];

    const handleDayClick = (day: number) => {
        if (selectedDay === day) {
            // Toggle expansion or set true? Req says "Upon tapping again... it should expand".
            // If already expanded, maybe nothing or collapse? Let's toggle for better UX.
            setIsExpanded(!isExpanded);
        } else {
            setSelectedDay(day);
            setIsExpanded(false);
        }
    };

    const getCrewDayColor = (day: number) => {
        // Mock logic to match screenshot colors approximately
        // 5, 25, 30: Green
        // 6, 8, 13, 27: Yellow
        // 3: Green
        // 11, 16, 20: Red
        const greenDays = [1, 3, 5, 9, 12, 14, 18, 20, 25, 30]; // Added 20 as green to match row 3? Wait, screenshot row 3 day 20 is GREEN.
        const yellowDays = [2, 6, 7, 8, 10, 13, 15, 19, 21, 23, 26, 27, 28];
        const redDays = [11, 17, 29]; // 11 is red.

        if (greenDays.includes(day)) return 'bg-[#4CAF50]';
        if (yellowDays.includes(day)) return 'bg-[#F6C555]';
        if (redDays.includes(day)) return 'bg-[#FF6B6B]';

        // Random fallback
        const val = day % 3;
        if (val === 0) return 'bg-[#4CAF50]';
        if (val === 1) return 'bg-[#F6C555]';
        return 'bg-[#FF6B6B]';
    };

    return (
        <div className="flex-1 flex flex-col h-full overflow-hidden bg-white">
            {/* Header */}
            <header className="px-4 pt-12 pb-4 flex items-center justify-between z-10">
                <div className="w-20 flex justify-start">
                    <Button variant="ghost" onClick={onBack} className="-ml-2 gap-1 text-zinc-900 hover:bg-transparent">
                        <ChevronLeftIcon className="w-6 h-6" />
                    </Button>
                </div>
                <h1 className="flex-1 text-center text-lg font-bold text-zinc-900">
                    크루 달력
                </h1>
                <div className="w-20 flex justify-end">
                    <Button
                        variant="ghost"
                        onClick={onMyCalendarClick}
                        className="text-xs text-zinc-400 hover:text-zinc-600 font-medium px-0 gap-0"
                    >
                        나의 달력 <ChevronRightIcon className="w-4 h-4" />
                    </Button>
                </div>
            </header>

            <main className="flex-1 overflow-y-auto px-4 pb-[120px] flex flex-col items-center">

                <Calendar
                    className="mb-8"
                    month="December"
                    year={2025}
                    startDayOfWeek={0}
                    totalDays={31}
                    expandable={false}
                    hideHeader={false}
                    selectedDays={[selectedDay]}
                    isCollapsed={isExpanded}
                    onDayClick={handleDayClick}
                    headerRight={
                        <div className="flex items-center gap-2">
                            <span className="text-xs font-bold text-zinc-500">내 일정</span>
                            <div
                                onClick={() => setShowMySchedule(!showMySchedule)}
                                className={`w-10 h-6 rounded-full p-1 relative cursor-pointer transition-colors duration-200 ${showMySchedule ? 'bg-[#4CAF50]' : 'bg-zinc-300'}`}
                            >
                                <div className={`w-4 h-4 bg-white rounded-full shadow-sm transition-transform duration-200 ${showMySchedule ? 'translate-x-4' : 'translate-x-0'}`} />
                            </div>
                        </div>
                    }
                    renderDay={(day) => {
                        const isSelected = selectedDay === day;
                        const isToday = day === today;
                        const colorClass = getCrewDayColor(day);

                        const isConfirmed = showMySchedule && confirmedDays.includes(day);
                        const isPending = showMySchedule && pendingDays.includes(day);

                        // Base classes
                        let containerClasses = "w-full h-full flex flex-col items-center justify-start pt-1.5 transition-all duration-200 cursor-pointer text-sm font-bold rounded-[10px]";

                        if (isSelected) {
                            containerClasses += " bg-[#333333] text-white shadow-lg scale-105";
                        } else if (isToday) {
                            containerClasses += " bg-[#F4F4F5] text-zinc-900";
                        } else {
                            containerClasses += " text-zinc-500 hover:bg-zinc-100/50";
                        }

                        // Number Element (Circle if reserved)
                        let numberElement = <span>{day}</span>;
                        if (isConfirmed || isPending) {
                            const bg = isConfirmed ? 'bg-[#1E3A8A]' : 'bg-[#9CA3AF]';
                            const textColor = 'text-white';
                            // Note: If selected, container is Black.
                            // If we render a Navy Circle inside a Black Box, it works.
                            numberElement = (
                                <div className={`w-7 h-7 -mt-1 rounded-full ${bg} ${textColor} flex items-center justify-center text-xs shadow-sm`}>
                                    {day}
                                </div>
                            );
                        }

                        return (
                            <div className={containerClasses}>
                                {numberElement}
                                <div className={`w-2 h-2 rounded-full ${colorClass} mt-1`} />
                            </div>
                        );
                    }}
                />

                {/* Bottom Section: Legend or Expanded View */}
                {isExpanded ? (
                    <div className="w-full animate-in fade-in slide-in-from-bottom-4 duration-300">
                        {/* Expanded User List Card */}
                        <div className="w-full bg-[#F4F4F5] rounded-[20px] p-6 mb-3">
                            {/* Card Header */}
                            <div className="flex items-center gap-2 mb-6">
                                <div className="w-7 h-7 bg-[#1E3A8A] rounded-[8px] flex items-center justify-center shadow-sm">
                                    <Smile className="w-4 h-4 text-white" strokeWidth={2.5} />
                                </div>
                                <span className="font-bold text-zinc-900 text-sm">예약 완료</span>
                                <span className="text-xs text-zinc-400 mt-0.5">8명/20</span>
                            </div>

                            {/* Users Grid */}
                            <div className="grid grid-cols-2 gap-y-5 gap-x-4">
                                {Array.from({ length: 8 }).map((_, i) => (
                                    <div key={i} className="flex items-center gap-3">
                                        <div className="w-10 h-10 bg-zinc-300 rounded-full shrink-0" />
                                        <span className="text-sm font-bold text-zinc-700">홍대 이수현</span>
                                    </div>
                                ))}
                            </div>
                        </div>

                        {/* Change Link */}
                        <div className="w-full flex justify-end mb-6 pr-2">
                            <button className="text-xs font-medium text-zinc-500 flex items-center gap-1 hover:text-zinc-800 transition-colors">
                                예약 변경하러 가기 <ChevronRightIcon className="w-3 h-3" />
                            </button>
                        </div>

                        {/* Action Button */}
                        {confirmedDays.includes(selectedDay) || pendingDays.includes(selectedDay) ? (
                            <Button
                                variant="outline"
                                className="w-full h-14 bg-white border border-zinc-400 hover:bg-zinc-50 rounded-[20px] text-zinc-500 text-lg font-bold shadow-sm transition-all active:scale-[0.98]"
                            >
                                예약 취소하기
                            </Button>
                        ) : (
                            <Button className="w-full h-14 bg-[#162660] hover:bg-[#1E3A8A] rounded-[20px] text-white text-lg font-bold shadow-md transition-all active:scale-[0.98]">
                                예약하기
                            </Button>
                        )}
                    </div>
                ) : (
                    /* Default Legend Section */
                    <div className="w-full bg-[#F4F4F5] rounded-[20px] p-4 flex items-center justify-between px-2">
                        <div className="flex items-center gap-2">
                            <span className="font-bold text-xs text-zinc-900">인원수</span>
                        </div>

                        <div className="flex items-center gap-4">
                            <div className="flex items-center gap-1.5">
                                <div className="w-2 h-2 rounded-full bg-[#4CAF50]" />
                                <span className="text-[10px] font-medium text-zinc-500">~5</span>
                            </div>
                            <div className="flex items-center gap-1.5">
                                <div className="w-2 h-2 rounded-full bg-[#F6C555]" />
                                <span className="text-[10px] font-medium text-zinc-500">5~10</span>
                            </div>
                            <div className="flex items-center gap-1.5">
                                <div className="w-2 h-2 rounded-full bg-[#FF6B6B]" />
                                <span className="text-[10px] font-medium text-zinc-500">10~</span>
                            </div>
                        </div>
                    </div>
                )}

            </main>
        </div>
    );
}
