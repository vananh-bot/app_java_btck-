package DAO;

import Model.Comment;

import java.util.List;

public interface CommentDAOInterface {
    // Lấy danh sách comment theo task
    List<Comment> getByTaskId(int taskId);

    // Thêm comment
    boolean insert(Comment comment);

    // Xóa comment theo id
    boolean deleteById(int commentId);

    // Cập nhật comment
    boolean update(Comment comment);

    // Đếm comment theo task
    int countByTaskId(int taskId);
}
