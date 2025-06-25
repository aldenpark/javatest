import { query } from '../db.js';

export default {
  Query: {
    profiles: async () => {
      const result = await db.query('SELECT * FROM profiles WHERE active = true');
      return result.rows;
    },
    profile: async (_, { id }) => {
      const result = await db.query('SELECT * FROM profiles WHERE id = $1', [id]);
      return result.rows[0];
    }
  }
};
