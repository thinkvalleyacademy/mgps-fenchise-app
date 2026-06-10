import React, { useCallback, useContext, useEffect, useState } from 'react'
import { GetAll } from '../../../../apis/Academic CRUD/AC_CRUD'
import AuthContext from '../../../../context/student/AuthContext';


const ClassDropDown = ({index, id,label,name, onChange, value}) => {
  const [data, setData] = useState([]);
  const { token } = useContext(AuthContext);

  const fetchData = useCallback(async () => {
    try {
      const responseData = await GetAll("academic", token);
      setData(responseData.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  }, [token]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return (
    <>
    <div className="form-group col-md-12" key={index}>
	               <label htmlFor={id}>{label}:</label>
	                <select
	                  className="form-control"
	                  name={name}
	                  value={value ?? ""}
	                  onChange={onChange}
	                  required
	                >
	                  <option value="">Select class...</option>
	                  {data.map((result, index) => (
	                    <option key={result?.id ?? result?.className ?? index} value={result.className}>
                        {result.className}
                      </option>
	                  ))}
	                </select>
	              </div>
      
    </>
  )
}

export default ClassDropDown
