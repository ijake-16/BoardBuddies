import { Button } from '../components/Button';
import { Calendar } from '../components/Calendar';

interface HomeProps {
    onReservationClick: () => void;
    onTeamClick: () => void;
}

// Icons

const BellIcon = ({ className }: { className?: string }) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
        <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9" />
        <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0" />
    </svg>
);

const SettingsIcon = ({ className }: { className?: string }) => (
    <svg width="26" height="26" viewBox="0 0 26 26" fill="none" xmlns="http://www.w3.org/2000/svg" className={className}>
        <path fillRule="evenodd" clipRule="evenodd" d="M13 2.16675C14.6141 2.16675 15.9809 3.2258 16.4434 4.68696C17.8036 3.98079 19.519 4.19839 20.6603 5.33976C21.8017 6.48113 22.0193 8.19646 21.3131 9.55667C22.7743 10.0192 23.8333 11.3859 23.8333 13.0001C23.8333 14.6142 22.7743 15.981 21.3131 16.4435C22.0193 17.8037 21.8017 19.519 20.6603 20.6604C19.519 21.8018 17.8036 22.0194 16.4434 21.3132C15.9809 22.7744 14.6141 23.8334 13 23.8334C11.3859 23.8334 10.0191 22.7744 9.55660 21.3132C8.19638 22.0194 6.48105 21.8018 5.33968 20.6604C4.19831 19.519 3.98071 17.8037 4.68688 16.4435C3.22573 15.981 2.16667 14.6142 2.16667 13.0001C2.16667 11.3859 3.22573 10.0192 4.68688 9.55667C3.98071 8.19646 4.19831 6.48113 5.33968 5.33976C6.48105 4.19839 8.19638 3.98079 9.55660 4.68696C10.0191 3.2258 11.3859 2.16675 13 2.16675ZM13 4.57416C12.3352 4.57416 11.7963 5.11307 11.7963 5.77786V6.18882C11.7963 6.69897 11.4747 7.15371 10.9937 7.32372C10.7934 7.39451 10.5972 7.4759 10.4058 7.56749C9.94539 7.78784 9.39603 7.69381 9.03508 7.33286L8.74427 7.04205C8.27419 6.57198 7.51205 6.57198 7.04198 7.04205C6.57190 7.51213 6.57190 8.27427 7.04198 8.74435L7.33279 9.03516C7.69373 9.39610 7.78777 9.94546 7.56742 10.4059C7.47582 10.5973 7.39443 10.7935 7.32364 10.9938C7.15364 11.4748 6.69889 11.7964 6.18874 11.7964H5.77778C5.11300 11.7964 4.57408 12.3353 4.57408 13.0001C4.57408 13.6649 5.11300 14.2038 5.77778 14.2038H6.18874C6.69889 14.2038 7.15364 14.5254 7.32364 15.0064C7.39443 15.2067 7.47582 15.4029 7.56742 15.5943C7.78777 16.0547 7.69373 16.6041 7.33279 16.965L7.04198 17.2558C6.57190 17.7259 6.57190 18.4880 7.04198 18.9581C7.51205 19.4282 8.27419 19.4282 8.74427 18.9581L9.03508 18.6673C9.39603 18.3064 9.94539 18.2123 10.4058 18.4327C10.5972 18.5243 10.7934 18.6057 10.9937 18.6764C11.4747 18.8465 11.7963 19.3012 11.7963 19.8113V20.2223C11.7963 20.8871 12.3352 21.4260 13 21.4260C13.6648 21.4260 14.2037 20.8871 14.2037 20.2223V19.8113C14.2037 19.3012 14.5253 18.8465 15.0063 18.6764C15.2066 18.6057 15.4028 18.5243 15.5942 18.4327C16.0546 18.2123 16.6040 18.3064 16.9649 18.6673L17.2557 18.9581C17.7258 19.4282 18.4880 19.4282 18.9580 18.9581C19.4281 18.4880 19.4281 17.7259 18.9580 17.2558L18.6672 16.965C18.3063 16.6041 18.2122 16.0547 18.4326 15.5943C18.5242 15.4029 18.6056 15.2067 18.6764 15.0064C18.8464 14.5254 19.3011 14.2038 19.8113 14.2038H20.2222C20.8870 14.2038 21.4259 13.6649 21.4259 13.0001C21.4259 12.3353 20.8870 11.7964 20.2222 11.7964H19.8113C19.3011 11.7964 18.8464 11.4748 18.6764 10.9938C18.6056 10.7935 18.5242 10.5973 18.4326 10.4059C18.2122 9.94546 18.3063 9.39610 18.6672 9.03516L18.9580 8.74435C19.4281 8.27427 19.4281 7.51213 18.9580 7.04205C18.4880 6.57198 17.7258 6.57198 17.2557 7.04205L16.9649 7.33286C16.6040 7.69381 16.0546 7.78784 15.5942 7.56749C15.4028 7.47590 15.2066 7.39451 15.0063 7.32372C14.5253 7.15371 14.2037 6.69897 14.2037 6.18882V5.77786C14.2037 5.11307 13.6648 4.57416 13 4.57416ZM13 10.5927C14.3296 10.5927 15.4074 11.6705 15.4074 13.0001C15.4074 14.3297 14.3296 15.4075 13 15.4075C11.6704 15.4075 10.5926 14.3297 10.5926 13.0001C10.5926 11.6705 11.6704 10.5927 13 10.5927Z" fill="#333333" />
    </svg>
);

const CircleArrowRightIcon = ({ className }: { className?: string }) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
        <circle cx="12" cy="12" r="10" className="opacity-20" />
        <path d="m10 8 4 4-4 4" />
    </svg>
);

const CheckSquareIcon = ({ className }: { className?: string }) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
        <polyline points="9 11 12 14 22 4" />
        <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" />
    </svg>
);

const SnowflakeDecorIcon = ({ className }: { className?: string }) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
        <path d="M2 12h20" />
        <path d="M12 2v20" />
        <path d="m4.93 4.93 14.14 14.14" />
        <path d="m4.93 19.07 14.14-14.14" />
    </svg>
);

export default function Home({ onReservationClick, onTeamClick }: HomeProps) {
    return (
        <div className="flex-1 flex flex-col h-full overflow-hidden">
            {/* Header */}
            <header className="px-6 pt-12 pb-4 flex items-center justify-between bg-white dark:bg-zinc-950 z-10">
                <div className="flex items-center gap-2">
                    <h1 className="text-[24px] font-normal tracking-tight" style={{ fontFamily: '"Joti One", serif' }}>BoardBuddy</h1>
                    {/* Shark Image removed due to missing file */}
                </div>
                <div className="flex items-center gap-2">
                    <Button variant="ghost" size="icon" className="text-zinc-900 dark:text-zinc-100 cursor-pointer">
                        <BellIcon className="w-[24px] h-[24px]" />
                    </Button>
                    <Button variant="ghost" size="icon" className="text-zinc-900 dark:text-zinc-100 cursor-pointer">
                        <SettingsIcon className="w-[24px] h-[24px]" />
                    </Button>
                </div>
            </header>

            {/* Content Area with bottom padding for menu */}
            <main className="flex-1 overflow-y-auto pb-[110px]">

                {/* Team Info */}
                <div className="px-6 mb-8">
                    <div className="text-sm text-zinc-500 font-medium mb-1">홍익대학교</div>
                    <div
                        onClick={onTeamClick}
                        className="flex items-center gap-2 cursor-pointer hover:opacity-80 transition-opacity"
                    >
                        <h2 className="text-2xl font-bold">Team 401</h2>
                        <CircleArrowRightIcon className="w-5 h-5 text-[#FCD34D]" />
                    </div>
                </div>

                {/* Action Cards */}
                <div className="px-8 grid grid-cols-2 gap-6 mb-8">
                    {/* Reservation Card */}
                    <button
                        onClick={onReservationClick}
                        className="bg-[#FCD34D] aspect-square rounded-[30px] p-6 flex flex-col items-center justify-center gap-3 text-zinc-900 hover:brightness-95 transition-all shadow-sm"
                    >
                        <div className="bg-white p-3 rounded-full shadow-sm">
                            <CheckSquareIcon className="w-12 h-12 text-zinc-900" />
                        </div>
                        <span className="font-bold text-xl">예약하기</span>
                    </button>

                    {/* Upcoming Schedule Card */}
                    <button
                        onClick={onReservationClick}
                        className="bg-[#D6E6F5] aspect-square rounded-[30px] p-5 flex flex-col hover:brightness-95 transition-all shadow-sm overflow-hidden relative text-left"
                    >
                        <div className="text-zinc-500 font-bold text-sm mb-3 w-full">다가오는 일정</div>

                        <div className="bg-white/80 backdrop-blur-sm rounded-xl p-4 w-full flex-1 flex flex-col justify-center gap-1 border-l-4 border-[#1E3A8A]">
                            <div className="text-lg font-bold text-zinc-900">12월 30일</div>
                            <div className="text-sm text-zinc-500 font-medium">예약 확정</div>
                        </div>
                    </button>
                </div>

                {/* Calendar Section */}
                <div className="px-8 mb-12">
                    <div className="bg-[#F3E5D8] rounded-[30px] p-6 shadow-sm">
                        <Calendar
                            month="December"
                            year={2025}
                            startDayOfWeek={1}
                            totalDays={31}
                            hideHeader={true}
                            maxWeeks={2}
                            renderDay={(day) => {
                                // Dummy dots for visualization
                                const hasDot = [5, 12, 19].includes(day);
                                const hasBlueDot = [2, 3].includes(day);

                                return (
                                    <div className="w-8 h-8 flex flex-col items-center justify-center relative">
                                        <span className={`text-sm font-medium text-zinc-500`}>{day}</span>
                                        {hasDot && (
                                            <div className="w-2 h-2 rounded-full bg-[#1E3A8A] opacity-60 absolute bottom-0" />
                                        )}
                                        {hasBlueDot && (
                                            <div className="w-2 h-2 rounded-full bg-[#1E3A8A] absolute bottom-0" />
                                        )}
                                    </div>
                                );
                            }}
                        />
                    </div>
                </div>

                {/* Weather Banner */}
                <div className="w-full bg-gradient-to-r from-[#F8CACC] to-[#A0C4FF] min-h-[120px] flex items-center justify-between relative overflow-hidden">
                    {/* Background decoration */}
                    <div className="absolute left-2 bottom-[-10px] opacity-60">
                        <SnowflakeDecorIcon className="w-32 h-32 text-white" />
                    </div>
                    <div className="absolute left-20 top-[-20px] opacity-40">
                        <SnowflakeDecorIcon className="w-16 h-16 text-white" />
                    </div>

                    <div className="z-10 pl-8 py-6">
                        <div className="font-bold text-lg text-white">휘닉스파크</div>
                        <div className="text-sm text-white/90">2025. 11. 11 Tue</div>
                    </div>
                    <div className="z-10 pr-8 text-5xl font-light text-white">
                        33<span className="text-2xl align-top">°</span>
                    </div>
                </div>

            </main>
        </div>
    );
}
