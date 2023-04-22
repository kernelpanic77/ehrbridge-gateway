import React, { useState, useEffect } from 'react';
import { Routes, Route, useNavigate } from 'react-router-dom';

import PatientRegister from './components/PatientRegister';
import HospitalRegister from './components/HospitalRegister';
import DoctorRegister from './components/DoctorRegister';

const App = () => {
 const navigate = useNavigate();
 return (
    <>
    <div className="container">
    <header d>
      <h1>Gateway Dashboard</h1>
        <button style={{marginRight:"10px"}} onClick={() => navigate('/hospital')}>Hospital </button>
        <button style={{marginRight:"10px"}} onClick={() => navigate('/doctor')}>Doctor</button>
        <button style={{marginRight:"10px"}} onClick={() => navigate('/patient')}>Patient</button>
    </header>
    </div>
       <Routes>
            <Route path="/" element={<> </>} />
            <Route path="/patient" element={<PatientRegister />} />
            <Route path="/hospital" element={<HospitalRegister />} />
            <Route path="/doctor" element={<DoctorRegister />} />
       </Routes>
    </>
 );
};

export default App;