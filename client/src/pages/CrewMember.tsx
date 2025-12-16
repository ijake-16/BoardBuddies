import { Button } from '../components/Button';
import { ChevronLeftIcon, Trash2Icon, CheckIcon, XIcon, UserIcon } from 'lucide-react';
import { useState } from 'react';

interface CrewMemberProps {
    onBack: () => void;
}

// Official Roles
// Guest: Before admitting (Applicants)
// Member: Default role
// Admin: Manager
// President: Chief Manager
type Role = 'PRESIDENT' | 'ADMIN' | 'MEMBER' | 'GUEST';

interface Member {
    id: string;
    name: string;
    role: Role;
    studentId: string;
}

interface Applicant {
    id: string;
    name: string;
    studentId: string;
    requestDate: string;
}

// Mock Data
const MOCK_MEMBERS: Member[] = [
    { id: '1', name: '김철수', role: 'PRESIDENT', studentId: '20201234' },
    { id: '2', name: '이영희', role: 'ADMIN', studentId: '20215678' },
    { id: '3', name: '박지성', role: 'ADMIN', studentId: '20191111' },
    { id: '4', name: '손흥민', role: 'MEMBER', studentId: '20222222' },
    { id: '5', name: '김연아', role: 'MEMBER', studentId: '20233333' },
    { id: '6', name: 'BTS', role: 'MEMBER', studentId: '20244444' },
];

const MOCK_APPLICANTS: Applicant[] = [
    { id: '101', name: 'Newbie', studentId: '20250001', requestDate: '2025-03-01' },
    { id: '102', name: 'Wannabe', studentId: '20250002', requestDate: '2025-03-02' },
];

export default function CrewMember({ onBack }: CrewMemberProps) {
    // State to toggle between ADMIN (Manager view) and MEMBER (General view) for demonstration
    const [currentUserRole, setCurrentUserRole] = useState<'ADMIN' | 'MEMBER'>('ADMIN');

    const [activeTab, setActiveTab] = useState<'members' | 'applicants'>('members');
    const [members, setMembers] = useState<Member[]>(MOCK_MEMBERS);
    const [applicants, setApplicants] = useState<Applicant[]>(MOCK_APPLICANTS);

    const handleDeleteMember = (id: string, name: string) => {
        if (window.confirm(`${name}님을 크루에서 삭제하시겠습니까?`)) {
            setMembers(members.filter(m => m.id !== id));
        }
    };

    const handleAcceptApplicant = (id: string) => {
        const applicant = applicants.find(a => a.id === id);
        if (applicant) {
            const newMember: Member = {
                id: applicant.id,
                name: applicant.name,
                role: 'MEMBER', // New members are always 'MEMBER' by default
                studentId: applicant.studentId
            };
            setMembers([...members, newMember]);
            setApplicants(applicants.filter(a => a.id !== id));
        }
    };

    const handleRejectApplicant = (id: string) => {
        if (window.confirm('가입 요청을 거절하시겠습니까?')) {
            setApplicants(applicants.filter(a => a.id !== id));
        }
    };

    const handleAcceptAll = () => {
        if (window.confirm('모든 가입 요청을 수락하시겠습니까?')) {
            const newMembers = applicants.map(a => ({
                id: a.id,
                name: a.name,
                role: 'MEMBER' as Role, // New members are always 'MEMBER' by default
                studentId: a.studentId
            }));
            setMembers([...members, ...newMembers]);
            setApplicants([]);
        }
    };

    return (
        <div className="flex-1 flex flex-col h-full overflow-hidden bg-white dark:bg-zinc-950">
            {/* Header */}
            <header className="px-6 pt-12 pb-4 flex items-center justify-between relative bg-white dark:bg-zinc-950 z-10">
                <Button variant="ghost" onClick={onBack} className="-ml-2 text-zinc-900 dark:text-zinc-100">
                    <ChevronLeftIcon className="w-6 h-6" />
                </Button>
                <h1 className=" flex-1 text-center text-lg font-bold text-zinc-900">Crew Members</h1>

                {/* Role Toggler for Demo */}
                <button
                    onClick={() => setCurrentUserRole(prev => prev === 'ADMIN' ? 'MEMBER' : 'ADMIN')}
                    className="text-xs px-2 py-1 bg-zinc-100 rounded border border-zinc-200"
                >
                    {currentUserRole} View
                </button>
            </header>

            {/* Tabs (Admin Only) */}
            {
                currentUserRole === 'ADMIN' && (
                    <div className="px-6 flex gap-4 border-b border-zinc-100">
                        <button
                            onClick={() => setActiveTab('members')}
                            className={`pb-3 text-sm font-bold transition-colors relative ${activeTab === 'members' ? 'text-zinc-900' : 'text-zinc-900/40'
                                }`}
                        >
                            전체 부원
                            {activeTab === 'members' && (
                                <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-zinc-900 rounded-full" />
                            )}
                        </button>
                        <button
                            onClick={() => setActiveTab('applicants')}
                            className={`pb-3 text-sm font-bold transition-colors relative ${activeTab === 'applicants' ? 'text-zinc-900' : 'text-zinc-900/40'
                                }`}
                        >
                            승인 대기
                            {applicants.length > 0 && (
                                <span className="ml-1.5 px-1.5 py-0.5 bg-red-500 text-white text-[10px] rounded-full">
                                    {applicants.length}
                                </span>
                            )}
                            {activeTab === 'applicants' && (
                                <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-zinc-900 rounded-full" />
                            )}
                        </button>
                    </div>
                )
            }

            <main className="flex-1 overflow-y-auto px-6 py-6 pb-28">

                {/* MEMBER LIST VIEW */}
                {(currentUserRole === 'MEMBER' || activeTab === 'members') && (
                    <div className="space-y-6">
                        {/* President (Captain) Section */}
                        <section>
                            <h3 className="text-sm font-bold text-zinc-400 mb-3">크루장</h3>
                            <div className="space-y-3">
                                {members.filter(m => m.role === 'PRESIDENT').map(member => (
                                    <div key={member.id} className="flex items-center justify-between">
                                        <div className="flex items-center gap-3">
                                            <div className="w-10 h-10 rounded-full bg-zinc-100 flex items-center justify-center">
                                                <UserIcon className="w-6 h-6 text-zinc-400" />
                                            </div>
                                            <div>
                                                <div className="flex items-center gap-2">
                                                    <p className="font-bold text-zinc-900">{member.name}</p>
                                                    <span className="text-[10px] px-1.5 py-0.5 bg-yellow-100 text-yellow-700 rounded-full font-bold">Captain</span>
                                                </div>
                                                {currentUserRole === 'ADMIN' && (
                                                    <p className="text-xs text-zinc-500">{member.studentId}</p>
                                                )}
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </section>

                        {/* Admin (Staff) Section */}
                        <section>
                            <h3 className="text-sm font-bold text-zinc-400 mb-3">운영진</h3>
                            <div className="space-y-3">
                                {members.filter(m => m.role === 'ADMIN').map(member => (
                                    <div key={member.id} className="flex items-center justify-between">
                                        <div className="flex items-center gap-3">
                                            <div className="w-10 h-10 rounded-full bg-zinc-100 flex items-center justify-center">
                                                <UserIcon className="w-6 h-6 text-zinc-400" />
                                            </div>
                                            <div>
                                                <div className="flex items-center gap-2">
                                                    <p className="font-bold text-zinc-900">{member.name}</p>
                                                    <span className="text-[10px] px-1.5 py-0.5 bg-blue-100 text-blue-600 rounded-full font-bold">Admin</span>
                                                </div>
                                                {currentUserRole === 'ADMIN' && (
                                                    <p className="text-xs text-zinc-500">{member.studentId}</p>
                                                )}
                                            </div>
                                        </div>
                                        {currentUserRole === 'ADMIN' && (
                                            <button
                                                onClick={() => handleDeleteMember(member.id, member.name)}
                                                className="p-2 text-zinc-400 hover:text-red-500 hover:bg-red-50 rounded-full transition-colors"
                                            >
                                                <Trash2Icon className="w-4 h-4" />
                                            </button>
                                        )}
                                    </div>
                                ))}
                            </div>
                        </section>

                        {/* General Member Section */}
                        <section>
                            <h3 className="text-sm font-bold text-zinc-400 mb-3">부원 ({members.filter(m => m.role === 'MEMBER').length})</h3>
                            <div className="space-y-3">
                                {members.filter(m => m.role === 'MEMBER').map(member => (
                                    <div key={member.id} className="flex items-center justify-between">
                                        <div className="flex items-center gap-3">
                                            <div className="w-10 h-10 rounded-full bg-zinc-100 flex items-center justify-center">
                                                <UserIcon className="w-6 h-6 text-zinc-400" />
                                            </div>
                                            <div>
                                                <p className="font-bold text-zinc-900">{member.name}</p>
                                                {currentUserRole === 'ADMIN' && (
                                                    <p className="text-xs text-zinc-500">{member.studentId}</p>
                                                )}
                                            </div>
                                        </div>
                                        {currentUserRole === 'ADMIN' && (
                                            <button
                                                onClick={() => handleDeleteMember(member.id, member.name)}
                                                className="p-2 text-zinc-400 hover:text-red-500 hover:bg-red-50 rounded-full transition-colors"
                                            >
                                                <Trash2Icon className="w-4 h-4" />
                                            </button>
                                        )}
                                    </div>
                                ))}
                            </div>
                        </section>
                    </div>
                )}

                {/* APPLICANT LIST VIEW (Admin Only) */}
                {currentUserRole === 'ADMIN' && activeTab === 'applicants' && (
                    <div className="space-y-6">
                        {applicants.length > 0 ? (
                            <>
                                <div className="flex items-center justify-between mb-2">
                                    <span className="text-sm text-zinc-500">총 {applicants.length}명의 신청이 있습니다.</span>
                                    <button
                                        onClick={handleAcceptAll}
                                        className="text-xs font-bold text-blue-600 bg-blue-50 px-3 py-1.5 rounded-lg hover:bg-blue-100 transition-colors"
                                    >
                                        일괄 수락
                                    </button>
                                </div>
                                <div className="space-y-3">
                                    {applicants.map(applicant => (
                                        <div key={applicant.id} className="bg-white border border-zinc-100 rounded-2xl p-4 shadow-sm">
                                            <div className="flex items-center justify-between mb-3">
                                                <div className="flex items-center gap-3">
                                                    <div className="w-10 h-10 rounded-full bg-zinc-100 flex items-center justify-center">
                                                        <UserIcon className="w-6 h-6 text-zinc-400" />
                                                    </div>
                                                    <div>
                                                        <div className="flex items-center gap-2">
                                                            <p className="font-bold text-zinc-900">{applicant.name}</p>
                                                            <span className="text-[10px] px-1.5 py-0.5 bg-pink-100 text-pink-600 rounded-full font-bold">New</span>
                                                        </div>
                                                        <p className="text-xs text-zinc-500">{applicant.studentId}</p>
                                                    </div>
                                                </div>
                                                <span className="text-[10px] text-zinc-400">{applicant.requestDate}</span>
                                            </div>
                                            <div className="flex gap-2">
                                                <button
                                                    onClick={() => handleAcceptApplicant(applicant.id)}
                                                    className="flex-1 bg-zinc-900 text-white text-sm font-bold h-10 rounded-xl hover:bg-zinc-800 transition-colors flex items-center justify-center gap-2"
                                                >
                                                    <CheckIcon className="w-4 h-4" />
                                                    수락
                                                </button>
                                                <button
                                                    onClick={() => handleRejectApplicant(applicant.id)}
                                                    className="flex-1 bg-zinc-100 text-zinc-600 text-sm font-bold h-10 rounded-xl hover:bg-zinc-200 transition-colors flex items-center justify-center gap-2"
                                                >
                                                    <XIcon className="w-4 h-4" />
                                                    거절
                                                </button>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </>
                        ) : (
                            <div className="flex flex-col items-center justify-center py-20 text-zinc-400">
                                <p>대기 중인 신청이 없습니다.</p>
                            </div>
                        )}
                    </div>
                )}

            </main>
        </div >
    );
}
