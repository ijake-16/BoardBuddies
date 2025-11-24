import { useState } from 'react';
import { LowerMenuBar } from './components/LowerMenuBar';
import Reservation from './Reservation';
import Home from './Home';

function App() {
  const [activeTab, setActiveTab] = useState<'home' | 'calendar' | 'edit' | 'heart' | 'user'>('home');
  const [currentView, setCurrentView] = useState<'home' | 'reservation'>('home');

  return (
    <div className="min-h-screen bg-zinc-100 dark:bg-zinc-900 font-sans text-zinc-900 dark:text-zinc-100 flex justify-center">
      {/* Mobile Container */}
      <div className="w-full max-w-md bg-white dark:bg-zinc-950 min-h-screen relative shadow-2xl overflow-hidden flex flex-col">

        {currentView === 'reservation' ? (
          <Reservation onBack={() => setCurrentView('home')} />
        ) : (
          <Home onReservationClick={() => setCurrentView('reservation')} />
        )}

        {/* Fixed Bottom Menu */}
        <LowerMenuBar activeTab={activeTab} onTabChange={setActiveTab} className="absolute" />
      </div>
    </div>
  );
}

export default App;
