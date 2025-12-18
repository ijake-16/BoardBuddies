import { useState, useEffect } from 'react';
import { ChevronLeftIcon, ChevronRightIcon, Smile } from 'lucide-react';
import { Button } from '../components/Button';
import { Calendar } from '../components/Calendar';
import { getCrewInfo, getReservationDetail } from '../services/crew';
import { ReservationDetail } from '../types/api';


interface ReservationStatsProps {
    onBack: () => void;
    onMyCalendarClick?: () => void;
}



export default function ReservationStats({ onBack, onMyCalendarClick }: ReservationStatsProps) {
    const todayDate = new Date();

    // Default to current date
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

    const [selectedDay, setSelectedDay] = useState<number>(isCurrentMonthView ? todayDay : 5); // Default to today logic or fallback
    const [isExpanded, setIsExpanded] = useState(false);
    const [showMySchedule, setShowMySchedule] = useState(false);
    const [crewCapacity, setCrewCapacity] = useState<number>(20); // Default, will update
    const [crewId, setCrewId] = useState<number | null>(null);

    // Store fetched reservation details
    // Key: "YYYY-MM-DD", Value: ReservationDetail
    const [detailsCache, setDetailsCache] = useState<Record<string, ReservationDetail | null>>({});

    const formatDate = (day: number) => {
        return `${currentYear}-${String(currentMonthIndex + 1).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
    };

    // 1. Fetch User Info to get Crew ID
    // 2. Then Fetch Crew Info using that ID
    useEffect(() => {
        const initData = async () => {
            try {
                // Import getUserInfo dynamically or if already imported
                const { getUserInfo } = await import('../services/user');
                const userData = await getUserInfo();

                if (userData.crew && userData.crew.crewId) {
                    const id = userData.crew.crewId;
                    setCrewId(id);

                    // Now fetch crew info for capacity
                    const crewData = await getCrewInfo(id);
                    if (crewData && crewData.dailyCapacity) {
                        setCrewCapacity(crewData.dailyCapacity);
                    }
                }
            } catch (error) {
                console.error("Failed to initialize stats:", error);
            }
        };
        initData();
    }, []); // Run once on mount

    const fetchDetailForDay = async (day: number) => {
        if (!crewId) return; // Wait for crewId

        const dateStr = formatDate(day);
        if (detailsCache[dateStr]) return; // Already cached

        try {
            const data = await getReservationDetail(crewId, dateStr);
            setDetailsCache(prev => ({ ...prev, [dateStr]: data }));
        } catch (error) {
            console.error(`Failed to fetch detail for ${dateStr}:`, error);
        }
    };

    // Effect to fetch detail when selectedDay changes
    useEffect(() => {
        if (selectedDay && crewId) {
            fetchDetailForDay(selectedDay);
        }
    }, [selectedDay, currentYear, currentMonthIndex, crewId]);


    // Mock Reservation Data for "My Schedule" (Blue/Grey dots)
    // These remain static mocks as per instructions "shouldn't be any yellow or red" refers to occupancy colors.
    const confirmedDays = [13, 14, 25, 26];
    const pendingDays = [27];

    const handleDayClick = (day: number) => {
        if (selectedDay === day) {
            setIsExpanded(!isExpanded);
        } else {
            setSelectedDay(day);
            setIsExpanded(false);
        }
    };

    // Navigation Bounds
    const minDate = new Date(2025, 9, 1); // October 2025
    const maxDate = new Date(2026, 4, 31); // May 2026

    const handlePrevMonth = () => {
        const newDate = new Date(currentYear, currentMonthIndex - 1, 1);
        if (newDate >= minDate) {
            setViewDate(newDate);
            setSelectedDay(0); // Reset selection or handle appropriately
        }
    };

    const handleNextMonth = () => {
        const newDate = new Date(currentYear, currentMonthIndex + 1, 1);
        if (newDate <= maxDate) {
            setViewDate(newDate);
            setSelectedDay(0); // Reset selection
        }
    };

    const canGoPrev = new Date(currentYear, currentMonthIndex - 1, 1) >= minDate;
    const canGoNext = new Date(currentYear, currentMonthIndex + 1, 1) <= maxDate;

    // Get Occupancy from cache or default to 0 (Green)
    const getOccupancy = (day: number) => {
        const dateStr = formatDate(day);
        return detailsCache[dateStr]?.booked ?? 0;
    };

    const getCrewDayColor = (day: number) => {
        const count = getOccupancy(day);
        const ratio = count / crewCapacity;

        if (ratio < 0.4) return 'bg-[#4CAF50]'; // Green < 40%
        if (ratio < 0.8) return 'bg-[#F6C555]'; // Yellow < 80%
        return 'bg-[#FF6B6B]'; // Red >= 80%
    };

    const currentDetail = selectedDay ? detailsCache[formatDate(selectedDay)] : null;
    const currentMemberCount = currentDetail?.booked ?? 0;
    // Fix: Ensure we fallback to empty array if member_list is undefined
    const currentMemberList = currentDetail?.member_list || [];

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
                    month={currentMonthName}
                    year={currentYear}
                    startDayOfWeek={firstDayOfMonth}
                    totalDays={daysInMonth}
                    expandable={false}
                    hideHeader={false}
                    selectedDays={[selectedDay]}
                    isCollapsed={isExpanded}
                    onDayClick={handleDayClick}
                    onPrevMonth={canGoPrev ? handlePrevMonth : undefined}
                    onNextMonth={canGoNext ? handleNextMonth : undefined}
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
                        const isToday = day === todayDay && isCurrentMonthView;
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
                                <span className="text-xs text-zinc-400 mt-0.5">{currentMemberCount}명/{crewCapacity}</span>
                            </div>

                            {/* Users Grid */}
                            <div className="grid grid-cols-2 gap-y-5 gap-x-4">
                                {currentMemberList.length > 0 ? (
                                    currentMemberList.map((member: { user_id: number; name: string; profile_image_url: string | null }) => (
                                        <div key={member.user_id} className="flex items-center gap-3">
                                            <div className="w-10 h-10 bg-zinc-300 rounded-full shrink-0 overflow-hidden">
                                                {member.profile_image_url ? (
                                                    <img src={member.profile_image_url} alt={member.name} className="w-full h-full object-cover" />
                                                ) : null}
                                            </div>
                                            <span className="text-sm font-bold text-zinc-700">{member.name}</span>
                                        </div>
                                    ))
                                ) : (
                                    <div className="col-span-2 text-center text-zinc-400 text-sm py-4">
                                        예약자가 없습니다.
                                    </div>
                                )}
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
                            <span className="font-bold text-xs text-zinc-900">혼잡도</span>
                        </div>

                        <div className="flex items-center gap-4">
                            <div className="flex items-center gap-1.5">
                                <div className="w-2 h-2 rounded-full bg-[#4CAF50]" />
                                <span className="text-[10px] font-medium text-zinc-500">
                                    {`~${Math.floor(crewCapacity * 0.4)} (~40%)`}
                                </span>
                            </div>
                            <div className="flex items-center gap-1.5">
                                <div className="w-2 h-2 rounded-full bg-[#F6C555]" />
                                <span className="text-[10px] font-medium text-zinc-500">
                                    {`${Math.floor(crewCapacity * 0.4)}~${Math.floor(crewCapacity * 0.8)} (40~80%)`}
                                </span>
                            </div>
                            <div className="flex items-center gap-1.5">
                                <div className="w-2 h-2 rounded-full bg-[#FF6B6B]" />
                                <span className="text-[10px] font-medium text-zinc-500">
                                    {`${Math.floor(crewCapacity * 0.8)}~ (80%~)`}
                                </span>
                            </div>
                        </div>
                    </div>
                )}
            </main>
        </div>
    );
}
