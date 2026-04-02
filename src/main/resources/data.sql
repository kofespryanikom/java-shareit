INSERT INTO booking_statuses (id, status)
VALUES (1, 'WAITING'),
       (2, 'APPROVED'),
       (3, 'REJECTED'),
       (4, 'CANCELLED')
ON CONFLICT (id) DO UPDATE SET status = EXCLUDED.status;