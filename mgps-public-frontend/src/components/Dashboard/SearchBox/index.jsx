import React, { useState } from 'react'
import './style.css'
import { FaSearch } from "react-icons/fa";


const SearchBox = ({setResults, listUser}) => {
    const [input, setInput] = useState("");
    console.log(listUser)

    const fetchData = (value) => {
      if (value === '') {
          setResults(false);
      } else {
          const processedValue = value.replace(/\s+/g, '');
          const lowerCaseValue = processedValue.toLowerCase(); 
          const results = listUser.filter((user) => {
              const lowerCaseName = (user.firstName + user.lastName).toLowerCase();
              return (
                  lowerCaseValue &&
                  user &&
                  lowerCaseName &&
                  lowerCaseName.includes(lowerCaseValue)
              );
          });
          setResults(results);
      }
  
  
  };
  
    const handleChange = (value) => {
      setInput(value);
      fetchData(value);
    };
  
    return (
      <div className='d-flex justify-content-center' style={{marginTop:`10px`}}>
       <div className="input-wrapper text-center">
        <FaSearch id="search-icon" />
        <input
          placeholder="Type to search..."
          value={input}
          onChange={(e) => handleChange(e.target.value)}
        />
      </div>
      </div>
     
    );
}

export default SearchBox
