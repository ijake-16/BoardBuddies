import { Button } from '../components/Button';
import { ChevronLeftIcon } from 'lucide-react';

interface SearchCrewProps {
    onBack: () => void;
}

export default function SearchCrew({ onBack }: SearchCrewProps) {
    // Mock data for recommended crews
    // Recommend Crew Based on User's School Data
    const recommendedCrews = [
        { id: 1, university: '홍익대학교', crewName: 'Team 401' },
        { id: 2, university: '한양대학교', crewName: '어머였더라' },
    ];
    //in Korean Alphabetical Order of University
    const allCrews = [
        { id: 3, university: '카이스트', crewName: 'KAKI' },
        { id: 4, university: '세종대학교', crewName: 'Jump' },
        { id: 5, university: '이화여자대학교', crewName: 'Snowhite' },
        { id: 6, university: '숙명여자대학교', crewName: 'Snowholic' },
    ];

    return (
        <div className="flex-1 flex flex-col h-full overflow-hidden bg-white">
            {/* Header */}
            <header className="px-6 pt-12 pb-4 flex items-center justify-between relative z-10">
                <Button variant="ghost" onClick={onBack} className="-ml-2 gap-1 text-zinc-900 hover:bg-transparent">
                    <ChevronLeftIcon className="w-8 h-8" />
                </Button>
                <h1 className="absolute left-1/2 -translate-x-1/2 text-xl font-bold text-zinc-900">크루 검색</h1>
                <div className="w-8" /> {/* Spacer for centering */}
            </header>

            {/* Content */}
            <main className="flex-1 overflow-y-auto px-6 pb-8">
                {/* Search Input */}
                <div className="mb-8">
                    <input type="text" className="w-full h-12 rounded-[8px] border-none px-8 text-zinc-900 focus:ring-2 focus:ring-blue-400 outline-none shadow-sm bg-white mt-4" />
                </div>

                {/* Recommended Section */}
                <div className="mb-6">
                    <h2 className="text-sm font-bold text-zinc-500 mb-4">추천</h2>

                    <div className="flex flex-col gap-6">
                        {recommendedCrews.map((crew) => (
                            <div key={crew.id} className="flex items-center justify-between">
                                <span className="font-bold text-zinc-900 text-base">{crew.university}</span>
                                <span className="text-zinc-500 text-sm">{crew.crewName}</span>
                            </div>
                        ))}
                    </div>
                </div>

                {/* Divider */}
                <div className="w-full h-[1px] bg-zinc-100 mb-6" />

                {/* All Crews Section */}
                <div className="mb-4">
                    <h2 className="text-sm font-bold text-zinc-500 mb-4">전체 크루</h2>

                    <div className="flex flex-col gap-6">
                        {allCrews.map((crew) => (
                            <div key={crew.id} className="flex items-center justify-between">
                                <span className="font-bold text-zinc-900 text-base">{crew.university}</span>
                                <span className="text-zinc-500 text-sm">{crew.crewName}</span>
                            </div>
                        ))}
                    </div>
                </div>
            </main>
        </div>
    );
}
