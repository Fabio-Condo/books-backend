package com.booksApiv2.constant;

public class Authority {
    public static final String[] USER_AUTHORITIES = { "book:read", "comment:create", "comment:read","comment:update","comment:delete" };  
    public static final String[] HR_AUTHORITIES = { "user:read", "user:update" , "book:read", "book:update", "author:read", "author:update" };    
    public static final String[] MANAGER_AUTHORITIES = { "user:read", "user:update" , "book:read", "book:update", "author:read", "author:update" };
    public static final String[] ADMIN_AUTHORITIES = { "user:read", "user:create", "user:update", "book:read", "book:create", "book:update", "author:read", "author:create", "author:update"};
    public static final String[] SUPER_ADMIN_AUTHORITIES = { "user:read", "user:create", "user:update", "user:delete" , "book:read", "book:create", "book:update", "book:delete", "author:read", "author:create", "author:update", "author:delete" };
}
