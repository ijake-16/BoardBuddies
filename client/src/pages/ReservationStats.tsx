
import { ChevronLeftIcon } from 'lucide-react';
import { Button } from '../components/Button';
import { Calendar } from '../components/Calendar';


interface ReservationStatsProps {
    onBack: () => void;
}


import { PageBackground } from '../components/PageBackground';

export default function ReservationStats({ onBack }: ReservationStatsProps) {
    // Dummy data generation
    const getDayColor = (day: number) => {
        // Deterministic random-like based on day
        const val = (day * 7 + 3) % 20;

        if (val < 5) return 'bg-[#FF6B6B]'; // Red
        if (val < 10) return 'bg-[#F6C555]'; // Yellow
        return 'bg-[#A0D2EB]'; // Blue
    };

    return (
        <div className="flex-1 flex flex-col h-full overflow-hidden bg-white">
            {/* Header */}
            <header className="px-6 pt-12 pb-4 flex items-center justify-between z-10">
                <Button variant="ghost" onClick={onBack} className="-ml-2 gap-1 text-zinc-500 hover:text-zinc-900">
                    <ChevronLeftIcon className="w-6 h-6" />
                </Button>
            </header>

            <main className="flex-1 overflow-y-auto px-6 pb-[120px] flex flex-col items-center">

                {/* Page Title */}
                <div className="w-full mb-6">
                    <h1 className="text-3xl font-black italic text-zinc-900 font-['Joti_One']">December</h1>
                    <p className="text-sm font-medium text-zinc-900">2025</p>
                </div>

                {/* Grey Container */}
                <div className="w-full bg-[#F4F4F5] rounded-[30px] p-4 mb-8">
                    {/* Header Pill */}
                    <div className="w-full bg-white rounded-2xl py-3 shadow-sm mb-4 flex justify-center items-center">
                        <h2 className="text-base font-bold text-zinc-900">크루 달력</h2>
                    </div>

                    {/* Calendar Card */}
                    <div className="w-full bg-white rounded-[30px] p-6 shadow-sm">
                        <Calendar
                            month="December"
                            year={2025}
                            startDayOfWeek={1}
                            totalDays={31}
                            expandable={true}
                            hideHeader={true}
                            renderDay={(day) => (
                                <div className={`w-10 h-10 rounded-full flex items-center justify-center text-sm font-bold text-zinc-800 shadow-sm ${getDayColor(day)}`}>
                                    {day}
                                </div>
                            )}
                        />

                        {/* Legend Section */}
                        <div className="w-full mt-8 flex flex-row justify-center items-center gap-8 border-t pt-6 border-zinc-100">
                            <h3 className="font-bold text-base mr-2">인원수</h3>

                            <div className="flex items-center gap-2">
                                <div className="w-4 h-4 rounded-full bg-[#FF6B6B] shadow-sm" />
                                <span className="text-xs font-medium text-zinc-500">~5</span>
                            </div>

                            <div className="flex items-center gap-2">
                                <div className="w-4 h-4 rounded-full bg-[#F6C555] shadow-sm" />
                                <span className="text-xs font-medium text-zinc-500">5~10</span>
                            </div>

                            <div className="flex items-center gap-2">
                                <div className="w-4 h-4 rounded-full bg-[#A0D2EB] shadow-sm" />
                                <span className="text-xs font-medium text-zinc-500">10~</span>
                            </div>
                        </div>
                    </div>
                </div>

            </main>
        </div>
    );
}
