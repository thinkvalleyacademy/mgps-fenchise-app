import React from 'react'

const OverViewCard = ({heading, data}) => {
  return (
    <div className="col-md-3">
    <div className="card h-100 shadow-sm">
      <div className="card-body">
        <h6 className="card-subtitle mb-2 text-muted">{heading}</h6>
        <h4 className="card-title">{data}</h4>
      </div>
    </div>
  </div>
  )
}

export default OverViewCard
