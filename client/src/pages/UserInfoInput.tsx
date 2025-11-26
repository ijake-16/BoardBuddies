import { Button } from '../components/Button';
import { ChevronLeftIcon, CheckIcon } from 'lucide-react';
import { useState } from 'react';

interface UserInfoInputProps {
    onBack: () => void;
    onSubmit: () => void;
}

export default function UserInfoInput({ onBack, onSubmit }: UserInfoInputProps) {
    const [gender, setGender] = useState<'female' | 'male' | null>(null);
    const [terms, setTerms] = useState({
        term1: false,
        term2: false,
        term3: false,
    });

    return (
        <div className="flex-1 flex flex-col h-full overflow-hidden bg-white dark:bg-zinc-950">

            {/* Top Section - Light Blue */}
            <div className="bg-[#D6E6F5] px-6 pt-12 pb-10 flex flex-col">
                {/* Header */}
                <header className="flex items-center justify-between relative mb-8">
                    <Button variant="ghost" onClick={onBack} className="-ml-2 text-zinc-900">
                        <ChevronLeftIcon className="w-6 h-6" />
                    </Button>
                    <h1 className="text-lg font-bold text-zinc-900 absolute left-1/2 -translate-x-1/2">회원 정보</h1>
                    <div className="w-10" />
                </header>

                {/* Form Fields */}
                <div className="space-y-6">
                    {/* Name */}
                    <div className="space-y-2">
                        <label className="text-sm font-bold text-zinc-800 ml-1">이름</label>
                        <input
                            type="text"
                            className="w-full h-12 rounded-[16px] border-none px-4 text-zinc-900 focus:ring-2 focus:ring-blue-400 outline-none shadow-sm bg-white"
                        />
                        <p className="text-xs text-zinc-600 ml-1">동아리 운영진이 식별 가능하도록 실명으로 작성해주세요.</p>
                    </div>

                    {/* School */}
                    <div className="space-y-2">
                        <label className="text-sm font-bold text-zinc-800 ml-1">학교</label>
                        <input
                            type="text"
                            className="w-full h-12 rounded-[16px] border-none px-4 text-zinc-900 focus:ring-2 focus:ring-blue-400 outline-none shadow-sm bg-white"
                        />
                    </div>

                    {/* Student ID */}
                    <div className="space-y-2">
                        <label className="text-sm font-bold text-zinc-800 ml-1">학번</label>
                        <input
                            type="text"
                            className="w-full h-12 rounded-[16px] border-none px-4 text-zinc-900 focus:ring-2 focus:ring-blue-400 outline-none shadow-sm bg-white"
                        />
                    </div>

                    {/* Phone */}
                    <div className="space-y-2">
                        <label className="text-sm font-bold text-zinc-800 ml-1">전화번호</label>
                        <input
                            type="tel"
                            className="w-full h-12 rounded-[16px] border-none px-4 text-zinc-900 focus:ring-2 focus:ring-blue-400 outline-none shadow-sm bg-white"
                        />
                    </div>

                    {/* Gender */}
                    <div className="space-y-2">
                        <label className="text-sm font-bold text-zinc-800 ml-1">성별</label>
                        <div className="flex items-center gap-8 mt-2 ml-1">
                            <label className="flex items-center gap-2 cursor-pointer">
                                <div className={`w-6 h-6 rounded-full flex items-center justify-center border ${gender === 'female' ? 'bg-blue-500 border-blue-500' : 'bg-zinc-300 border-zinc-300'}`}>
                                    {gender === 'female' && <CheckIcon className="w-4 h-4 text-white" />}
                                </div>
                                <input
                                    type="radio"
                                    name="gender"
                                    className="hidden"
                                    onChange={() => setGender('female')}
                                    checked={gender === 'female'}
                                />
                                <span className="text-zinc-800 font-medium">여자</span>
                            </label>
                            <label className="flex items-center gap-2 cursor-pointer">
                                <div className={`w-6 h-6 rounded-full flex items-center justify-center border ${gender === 'male' ? 'bg-blue-500 border-blue-500' : 'bg-zinc-300 border-zinc-300'}`}>
                                    {gender === 'male' && <CheckIcon className="w-4 h-4 text-white" />}
                                </div>
                                <input
                                    type="radio"
                                    name="gender"
                                    className="hidden"
                                    onChange={() => setGender('male')}
                                    checked={gender === 'male'}
                                />
                                <span className="text-zinc-800 font-medium">남자</span>
                            </label>
                        </div>
                    </div>
                </div>
            </div>

            {/* Bottom Section - White */}
            <div className="flex-1 bg-white px-6 py-8">
                <h3 className="text-sm font-bold text-zinc-800 mb-4 ml-1">약관</h3>
                <div className="space-y-4">
                    <label className="flex items-center gap-3 cursor-pointer">
                        <div className={`w-5 h-5 rounded-full flex items-center justify-center ${terms.term1 ? 'bg-blue-600' : 'bg-zinc-300'}`}>
                            <CheckIcon className="w-3 h-3 text-white" />
                        </div>
                        <input
                            type="checkbox"
                            className="hidden"
                            checked={terms.term1}
                            onChange={() => setTerms({ ...terms, term1: !terms.term1 })}
                        />
                        <span className="text-blue-600 font-medium text-sm">필수</span>
                    </label>

                    <label className="flex items-center gap-3 cursor-pointer">
                        <div className={`w-5 h-5 rounded-full flex items-center justify-center ${terms.term2 ? 'bg-blue-600' : 'bg-zinc-300'}`}>
                            <CheckIcon className="w-3 h-3 text-white" />
                        </div>
                        <input
                            type="checkbox"
                            className="hidden"
                            checked={terms.term2}
                            onChange={() => setTerms({ ...terms, term2: !terms.term2 })}
                        />
                        <span className="text-blue-600 font-medium text-sm">필수</span>
                    </label>

                    <label className="flex items-center gap-3 cursor-pointer">
                        <div className={`w-5 h-5 rounded-full flex items-center justify-center ${terms.term3 ? 'bg-blue-600' : 'bg-zinc-300'}`}>
                            <CheckIcon className="w-3 h-3 text-white" />
                        </div>
                        <input
                            type="checkbox"
                            className="hidden"
                            checked={terms.term3}
                            onChange={() => setTerms({ ...terms, term3: !terms.term3 })}
                        />
                        <span className="text-blue-600 font-medium text-sm">선택</span>
                    </label>
                </div>
            </div>

        </div>
    );
}
