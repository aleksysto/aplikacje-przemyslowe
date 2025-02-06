-- Insert Authors
INSERT INTO authors (id, name) VALUES (1, 'George Orwell'), (2, 'J.K. Rowling'), (3, 'J.R.R. Tolkien');

-- Insert Users
INSERT INTO users (id, username, password, email, full_name, user_type)
VALUES
    (1, 'admin', '$2a$10$7N7bW/b3D.DPXfZaMKkxje7i8hLQWtuL2Rm.s.B0a7.wFgM1kKZgK', 'admin@example.com', 'Admin User', 'ADMIN'),
    (2, 'john_doe', '$2a$10$eImiTxRBsHAYG7J5ShX1ReaG4NfFhX5UJsFqJ/ix/TmDkG/bKp3Xa', 'john@example.com', 'John Doe', 'USER');

-- Insert Books
INSERT INTO books (id, title, author_id, genre, description, publish_date, publisher, borrowed, borrowed_by_id)
VALUES
    (1, '1984', 1, 'Dystopian', 'A novel about a totalitarian regime.', '1949-06-08', 'Secker & Warburg', false, NULL),
    (2, 'Harry Potter and the Sorcerer''s Stone', 2, 'Fantasy', 'The first book in the Harry Potter series.', '1997-06-26', 'Bloomsbury', false, NULL),
    (3, 'The Hobbit', 3, 'Fantasy', 'A fantasy novel about Bilbo Baggins.', '1937-09-21', 'Allen & Unwin', false, NULL);

-- Insert Comments
INSERT INTO comments (id, text, author_id, book_id, created_at)
VALUES
    (1, 'Great book!', 2, 1, NOW()),
    (2, 'A childhood favorite!', 2, 2, NOW());

-- Insert Book Ratings
INSERT INTO book_ratings (id, book_id, user_id, rating)
VALUES
    (1, 1, 2, 5),
    (2, 2, 2, 4),
    (3, 3, 2, 5);
