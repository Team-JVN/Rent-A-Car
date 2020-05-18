import { Comment } from './comment';

export class Feedback {
    rating: Number;
    comments: Comment[];

    constructor(rating: Number, comments: Comment[]) {
        this.rating = rating;
        this.comments = comments;
    }

}