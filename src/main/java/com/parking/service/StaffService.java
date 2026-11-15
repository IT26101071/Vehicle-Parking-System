package com.parking.service;

import com.parking.model.Staff;
import java.sql.SQLException;
import java.util.List;

public interface StaffService {
    Staff createStaff(Staff staff) throws SQLException;
    List<Staff> getAllStaff() throws SQLException;
    Staff getStaffById(Integer staffId) throws SQLException;
    boolean updateStaff(Integer staffId, Staff staff) throws SQLException;
    boolean deleteStaff(Integer staffId) throws SQLException;
}
