import { query } from '../db.js';

export default {
  Query: {
    aboutMe: async (_, { userId }) => {
      const result = await query('SELECT id, bio FROM about_me WHERE id = $1', [userId]);
      return result.rows[0];
    }
  }
};
