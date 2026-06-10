import React, { useState } from 'react';
import { runAllTests } from '../../E2ETests/E2ETestSuite';
import './E2ETestRunner.css';

const E2ETestRunner = () => {
  const [testResults, setTestResults] = useState(null);
  const [isRunning, setIsRunning] = useState(false);
  const [consoleOutput, setConsoleOutput] = useState([]);

  // Override console.log for capturing output
  const originalConsoleLog = console.log;

  const runTests = async () => {
    setIsRunning(true);
    setTestResults(null);
    setConsoleOutput([]);

    // Capture console output
    const logs = [];
    console.log = (...args) => {
      logs.push(args.join(' '));
      setConsoleOutput([...logs]);
    };

    try {
      const results = await runAllTests();
      setTestResults(results);
    } catch (error) {
      console.error('Test suite error:', error);
    } finally {
      // Restore original console.log
      console.log = originalConsoleLog;
      setIsRunning(false);
    }
  };

  return (
    <div className="e2e-test-runner">
      <div className="test-header">
        <h1>🧪 E2E Test Suite</h1>
        <p>School Management System - Frontend Testing with Mock API</p>
        <button 
          className={`run-button ${isRunning ? 'running' : ''}`}
          onClick={runTests}
          disabled={isRunning}
        >
          {isRunning ? '⏳ Running Tests...' : '▶️ Run All Tests'}
        </button>
      </div>

      {testResults && (
        <div className="test-summary">
          <div className="summary-card total">
            <h3>Total Tests</h3>
            <p className="number">{testResults.passed + testResults.failed}</p>
          </div>
          <div className="summary-card passed">
            <h3>✅ Passed</h3>
            <p className="number">{testResults.passed}</p>
          </div>
          <div className="summary-card failed">
            <h3>❌ Failed</h3>
            <p className="number">{testResults.failed}</p>
          </div>
        </div>
      )}

      {testResults && testResults.failed === 0 && (
        <div className="success-banner">
          🎉 All Tests Passed! 🎉
        </div>
      )}

      {testResults && testResults.failed > 0 && (
        <div className="failure-banner">
          ⚠️ {testResults.failed} Test(s) Failed
        </div>
      )}

      {testResults && (
        <div className="detailed-results">
          <h2>Detailed Results</h2>
          <div className="results-list">
            {testResults.tests.map((test, index) => (
              <div 
                key={index} 
                className={`result-item ${test.status.toLowerCase()}`}
              >
                <span className="result-icon">
                  {test.status === 'PASSED' ? '✅' : '❌'}
                </span>
                <span className="result-name">{test.name}</span>
                <span className={`result-status ${test.status.toLowerCase()}`}>
                  {test.status}
                </span>
              </div>
            ))}
          </div>
        </div>
      )}

      <div className="console-output">
        <h2>Console Output</h2>
        <div className="console-content">
          {consoleOutput.map((log, index) => (
            <div key={index} className="console-line">
              <pre>{log}</pre>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default E2ETestRunner;
