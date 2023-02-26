import React, { useState, useEffect, version } from 'react';
import Swal from 'sweetalert2';
import axios from "axios";
import Header from './Header';

const Dashboard = ({ setIsAuthenticated }) => {
  const [questions, setQuestions] = useState([]);
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [isAdding, setIsAdding] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [isViewing, setIsViewing] = useState(false);
  const [isGeneratingPaper, setIsGeneratingPaper] = useState(false);
  useEffect(() => {
    const authorId = JSON.parse(localStorage.getItem('author_id'));
    console.log("useEffect");
    axios.get("http://localhost:8090/item/author/"+authorId).then((response) => {
      setQuestions(response.data);
    });
  }, []);

  const handleView = (id,version) => {
    const [question] = questions.filter(question => question.id === id && question.version == version);

    setSelectedEmployee(question);
    setIsViewing(true);
  };


  const handleEdit = id => {
    const [question] = questions.filter(question => question.id === id);

    setSelectedEmployee(question);
    setIsEditing(true);
  };

  const handleDelete = id => {
    Swal.fire({
      icon: 'warning',
      title: 'Are you sure?',
      text: "You won't be able to revert this!",
      showCancelButton: true,
      confirmButtonText: 'Yes, delete it!',
      cancelButtonText: 'No, cancel!',
    }).then(result => {
      if (result.value) {
        const [question] = questions.filter(question => question.id === id);
        // axios request to http://localhost:8090/item/delete/id
        axios.post("http://localhost:8090/item/delete/"+id).then((response) => {
          console.log(response);
          setQuestions(questions.filter(question => question.id !== id));
          Swal.fire('Deleted!', 'Your question has been deleted.', 'success');
        });
      }
    });
  };
  return (
    <div className="container">
      {!isAdding && !isEditing && !isViewing && !isGeneratingPaper && (
        <>
          <Header
            setIsAdding={setIsAdding}
            setIsAuthenticated={setIsAuthenticated}
            setIsGeneratingPaper={setIsGeneratingPaper}
          />
        </>
      )}
    </div>
  );
};

export default Dashboard;
