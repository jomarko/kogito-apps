module.exports = HRInterview = {
  $schema: 'https://json-schema.org/draft/2019-09/schema',
  type: 'object',
  properties: {
    candidate: {
      type: 'object',
      properties: {
        email: { type: 'string' },
        name: { type: 'string' },
        salary: { type: 'integer' },
        skills: { type: 'string' }
      },
      input: true
    },
    approve: { type: 'boolean', output: true }
  },
  phases: ['complete', 'start', 'claim', 'release']
};
