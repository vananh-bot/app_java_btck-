package Service;

import DAO.UserProjectDAO;
import DTO.MemberDTO;
import java.util.List;
import java.util.stream.Collectors;

public class MemberService {
    private final UserProjectDAO userProjectDAO;

    public MemberService() {
        this.userProjectDAO = new UserProjectDAO();
    }

    /**
     * Lấy toàn bộ thành viên chính thức (Owner & Member) của một dự án
     */
    public List<MemberDTO> getProjectMembers(int projectId) {
        // Lưu ý: Bạn cần bổ sung hàm 'getMembersFullInfo' vào UserProjectDAO
        // thực hiện JOIN bảng 'user' và 'user_project'
        return userProjectDAO.getMembersFullInfo(projectId);
    }

    /**
     * Logic tìm kiếm thành viên trên danh sách hiện có (không cần chọc DB liên tục)
     */
    public List<MemberDTO> filterMembers(List<MemberDTO> allMembers, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return allMembers;
        }
        String lowerKey = keyword.toLowerCase().trim();
        return allMembers.stream()
                .filter(m -> m.getName().toLowerCase().contains(lowerKey) ||
                        m.getEmail().toLowerCase().contains(lowerKey))
                .collect(Collectors.toList());
    }
}