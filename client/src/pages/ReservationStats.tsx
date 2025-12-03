import { useState } from 'react';
import { ChevronLeftIcon } from 'lucide-react';
import { Button } from '../components/Button';
import { Calendar } from '../components/Calendar';


interface ReservationStatsProps {
    onBack: () => void;
    initialView?: 'crew' | 'my';
}



export default function ReservationStats({ onBack, initialView = 'crew' }: ReservationStatsProps) {
    const [view, setView] = useState<'crew' | 'my'>(initialView);

    // Dummy data generation for Crew View
    const getCrewDayColor = (day: number) => {
        const val = (day * 7 + 3) % 20;
        if (val < 5) return 'bg-[#FF6B6B]'; // Red (10~)
        if (val < 10) return 'bg-[#F6C555]'; // Yellow (5~10)
        return 'bg-[#4CAF50]'; // Green (~5) - Updated to Green as per plan/screenshot
    };

    // Dummy data generation for My View
    const getMyDayStatus = (day: number) => {
        if ([3, 10, 17, 24].includes(day)) return 'confirmed'; // Dark Blue
        if ([20, 27, 31].includes(day)) return 'pending'; // Grey
        return null;
    };

    return (
        <div className="flex-1 flex flex-col h-full overflow-hidden bg-white">
            {/* Header */}
            <header className="px-6 pt-12 pb-4 flex items-center justify-between z-10">
                <Button variant="ghost" onClick={onBack} className="-ml-2 gap-1 text-zinc-900 hover:bg-transparent">
                    <ChevronLeftIcon className="w-8 h-8" />
                </Button>
                <h1 className="absolute left-1/2 -translate-x-1/2 text-xl font-bold text-zinc-900">
                    {view === 'crew' ? '크루 달력' : '나의 달력'}
                </h1>
                <div className="w-8" />
            </header>

            <main className="flex-1 overflow-y-auto px-6 pb-[120px] flex flex-col items-center">

                {/* Page Title (Year/Month) - Kept consistent with design */}
                {/* Note: The screenshot shows Year/Month inside the grey container's header area in a specific way, 
                    but the previous design had it large outside. The screenshot shows "2025" and "December" inside.
                    I will adapt to the screenshot layout: 
                    The grey container wraps everything. 
                    Inside: 
                    - Top row: Year pill (left), Toggle button (right).
                    - Calendar navigation row.
                    - Calendar grid.
                    
                    Wait, looking at the screenshot again:
                    - There is a grey container.
                    - Inside top: "2025" (white pill) on left. Toggle button (grey) on right.
                    - Then Calendar component.
                    
                    Let's adjust the layout to match the screenshot more closely.
                */}

                {/* Grey Container */}
                <div className="w-full bg-[#F4F4F5] rounded-[30px] p-6 mb-8">
                    {/* Top Control Row */}
                    <div className="flex justify-between items-center mb-4">
                        <div className="bg-white px-4 py-1.5 rounded-full shadow-sm text-sm font-bold text-zinc-900">
                            2025
                        </div>
                        <div className="flex items-center gap-2">
                            <span className={`text-sm font-bold transition-colors ${view === 'crew' ? 'text-zinc-900' : 'text-zinc-400'}`}>크루 달력</span>
                            <button
                                onClick={() => setView(view === 'crew' ? 'my' : 'crew')}
                                className={`w-12 h-7 rounded-full p-1 transition-colors duration-200 ease-in-out relative ${view === 'my' ? 'bg-[#4CAF50]' : 'bg-zinc-300'}`}
                            >
                                <div
                                    className={`w-5 h-5 bg-white rounded-full shadow-sm transition-transform duration-200 ease-in-out ${view === 'my' ? 'translate-x-5' : 'translate-x-0'}`}
                                />
                            </button>
                            <span className={`text-sm font-bold transition-colors ${view === 'my' ? 'text-zinc-900' : 'text-zinc-400'}`}>나의 달력</span>
                        </div>
                    </div>

                    {/* Calendar Card */}
                    <div className="w-full bg-white rounded-[30px] p-6 shadow-sm">
                        <Calendar
                            month="December"
                            year={2025}
                            startDayOfWeek={0} // Screenshot shows Sun start
                            totalDays={31}
                            expandable={false} // Screenshot doesn't show expand button
                            hideHeader={false} // Calendar component handles the "December" header with arrows
                            renderDay={(day) => {
                                if (view === 'crew') {
                                    const colorClass = getCrewDayColor(day);
                                    return (
                                        <div className="w-8 h-10 flex flex-col items-center justify-start pt-1 relative">
                                            <span className="text-sm font-medium text-zinc-500 z-10">{day}</span>
                                            <div className={`w-3 h-3 rounded-full ${colorClass} mt-1`} />
                                        </div>
                                    );
                                } else {
                                    const status = getMyDayStatus(day);
                                    let dotClass = '';
                                    if (status === 'confirmed') dotClass = 'bg-[#1E3A8A]';
                                    else if (status === 'pending') dotClass = 'bg-[#9CA3AF]';

                                    return (
                                        <div className="w-8 h-10 flex flex-col items-center justify-start pt-1 relative">
                                            <span className="text-sm font-medium text-zinc-500 z-10">{day}</span>
                                            {status && <div className={`w-3 h-3 rounded-full ${dotClass} mt-1`} />}
                                        </div>
                                    );
                                }
                            }}
                        />
                    </div>
                </div>

                {/* Legend / Footer Section */}
                <div className="w-full bg-[#F4F4F5] rounded-[20px] p-4">
                    {view === 'crew' ? (
                        <div className="flex items-center justify-between px-2">
                            <span className="font-bold text-sm">인원수</span>
                            <div className="flex items-center gap-4">
                                <div className="flex items-center gap-1.5">
                                    <div className="w-3 h-3 rounded-full bg-[#4CAF50]" />
                                    <span className="text-xs font-bold text-zinc-900">~5</span>
                                </div>
                                <div className="flex items-center gap-1.5">
                                    <div className="w-3 h-3 rounded-full bg-[#F6C555]" />
                                    <span className="text-xs font-bold text-zinc-900">5~10</span>
                                </div>
                                <div className="flex items-center gap-1.5">
                                    <div className="w-3 h-3 rounded-full bg-[#FF6B6B]" />
                                    <span className="text-xs font-bold text-zinc-900">10~</span>
                                </div>
                            </div>
                        </div>
                    ) : (
                        <div className="flex flex-col gap-4">
                            <div className="flex items-center justify-between px-2">
                                <span className="font-bold text-sm">예약 상태</span>
                                <div className="flex items-center gap-4">
                                    <div className="flex items-center gap-1.5">
                                        <div className="w-3 h-3 rounded-full bg-[#1E3A8A]" />
                                        <span className="text-xs font-bold text-zinc-900">예약 확정</span>
                                    </div>
                                    <div className="flex items-center gap-1.5">
                                        <div className="w-3 h-3 rounded-full bg-[#9CA3AF]" />
                                        <span className="text-xs font-bold text-zinc-900">예약 대기</span>
                                    </div>
                                </div>
                            </div>
                            <div className="w-full h-[1px] bg-white" />
                            <div className="flex items-center justify-between px-2">
                                <span className="font-bold text-sm">시즌방 누적 횟수 :</span>
                                <span className="font-bold text-sm">13박</span>
                            </div>
                        </div>
                    )}
                </div>

            </main>
        </div>
    );
}
