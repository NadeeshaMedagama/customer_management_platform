module.exports = {
  testEnvironment: 'node',
  roots: ['<rootDir>/tests'],
  testMatch: ['**/*.test.cjs'],
  collectCoverageFrom: [
    'tests/**/*.cjs'
  ]
};


