CREATE PROCEDURE delete_users_in_range(IN start_id BIGINT, IN end_id BIGINT)
BEGIN
    DELETE FROM user WHERE id BETWEEN start_id AND end_id;
END
