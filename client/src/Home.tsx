import { Button } from './components/Button';

interface HomeProps {
    onReservationClick: () => void;
}

// Icons
const SearchIcon = ({ className }: { className?: string }) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
        <circle cx="11" cy="11" r="8" />
        <path d="m21 21-4.3-4.3" />
    </svg>
);

const BellIcon = ({ className }: { className?: string }) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
        <path d="M6 8a6 6 0 0 1 12 0c0 7 3 9 3 9H3s3-2 3-9" />
        <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0" />
    </svg>
);

const SettingsIcon = ({ className }: { className?: string }) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
        <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.1a2 2 0 0 1-1-1.72v-.51a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z" />
        <circle cx="12" cy="12" r="3" />
    </svg>
);

const CircleArrowRightIcon = ({ className }: { className?: string }) => (
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className={className}>
        <circle cx="12" cy="12" r="10" />
        <path d="m8 12 4 4 4-4" transform="rotate(-90 12 12)" />
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

export default function Home({ onReservationClick }: HomeProps) {
    return (
        <div className="flex-1 flex flex-col h-full overflow-hidden">
            {/* Header */}
            <header className="px-6 pt-12 pb-4 flex items-center justify-between bg-white dark:bg-zinc-950 z-10">
                <h1 className="text-[24px] font-normal tracking-tight" style={{ fontFamily: '"Joti One", serif' }}>BoardBuddy</h1>
                <div className="flex items-center gap-2">
                    <Button variant="ghost" size="icon" className="text-zinc-900 dark:text-zinc-100">
                        <SearchIcon className="w-[24px] h-[24px]" />
                    </Button>
                    <Button variant="ghost" size="icon" className="text-zinc-900 dark:text-zinc-100">
                        <BellIcon className="w-[24px] h-[24px]" />
                    </Button>
                    <Button variant="ghost" size="icon" className="text-zinc-900 dark:text-zinc-100">
                        <SettingsIcon className="w-[24px] h-[24px]" />
                    </Button>
                </div>
            </header>

            {/* Content Area with bottom padding for menu */}
            <main className="flex-1 overflow-y-auto pb-[110px]">

                {/* Team Info */}
                <div className="px-6 mb-8">
                    <div className="text-sm text-zinc-500 font-medium mb-1">홍익대학교</div>
                    <div className="flex items-center gap-2 cursor-pointer hover:opacity-80 transition-opacity mb-4">
                        <h2 className="text-2xl font-bold">Team 401</h2>
                        <CircleArrowRightIcon className="w-5 h-5 text-blue-300" />
                    </div>

                    {/* Progress Bar */}
                    <div className="h-4 w-full bg-blue-100 rounded-full overflow-hidden">
                        <div className="h-full bg-[#8da4ef] w-full rounded-full" />
                    </div>
                </div>

                {/* Action Cards */}
                <div className="px-6 grid grid-cols-2 gap-4 mb-12">
                    {/* Reservation Card */}
                    <button
                        onClick={onReservationClick}
                        className="bg-[#EBAEA2] aspect-[4/3] rounded-[20px] p-5 flex flex-col items-start justify-start text-zinc-800 hover:brightness-95 transition-all shadow-sm"
                    >
                        <div className="flex items-center gap-2">
                            <CheckSquareIcon className="w-5 h-5 opacity-70" />
                            <span className="font-medium text-base">예약하기</span>
                        </div>
                    </button>

                    {/* Yellow Placeholder Card */}
                    <button className="bg-[#F6E8B1] aspect-[4/3] rounded-[20px] p-5 flex flex-col items-start justify-start text-zinc-800 hover:brightness-95 transition-all shadow-sm">
                        <div className="flex items-center gap-2">
                            <CheckSquareIcon className="w-5 h-5 opacity-70" />
                            <span className="font-medium text-base">준비중</span>
                        </div>
                    </button>
                </div>

                {/* Weather Banner */}
                <div className="w-full bg-gradient-to-r from-[#bfdcf7] to-[#d4e7fa] min-h-[120px] flex items-center justify-between relative overflow-hidden">
                    {/* Background decoration */}
                    <div className="absolute left-2 bottom-[-10px] opacity-60">
                        <SnowflakeDecorIcon className="w-32 h-32 text-white" />
                    </div>
                    <div className="absolute left-20 top-[-20px] opacity-40">
                        <SnowflakeDecorIcon className="w-16 h-16 text-white" />
                    </div>

                    <div className="z-10 pl-8 py-6">
                        <div className="font-bold text-lg">휘닉스파크</div>
                        <div className="text-sm text-zinc-600">2025. 11. 11 Tue</div>
                    </div>
                    <div className="z-10 pr-8 text-5xl font-light text-zinc-700">
                        33<span className="text-2xl align-top">°</span>
                    </div>
                </div>

            </main>
        </div>
    );
}
