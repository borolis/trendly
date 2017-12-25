# -*- coding: utf-8 -*-
from sqlalchemy import Table, Column, Integer, String, DATETIME
from db import UserBase, VKBase


"""
 -----------------------------------------------------------------------------------------------------------------------
|ОБЩИЕ ПОЛЬЗОВАТЕЛЬСКИЕ МОДЕЛИ
 -----------------------------------------------------------------------------------------------------------------------
"""

class User(UserBase):
    __tablename__ = 'Users'
    __table_args__ = {'extend_existing': True}
    id = Column(Integer, primary_key=True)
    username = Column(String(16), unique=True)
    firstname = Column(String(16))
    surname =  Column(String(16))
    email = Column(String(24), unique=True)
    password = Column(String)
    vk_access_token = Column(String)
    payment_api_token = Column(String)

"""
 -----------------------------------------------------------------------------------------------------------------------
|МОДЕЛИ СВЯЗАННЫЕ С ВК
 -----------------------------------------------------------------------------------------------------------------------
"""
class VKProject(VKBase):
    __tablename__ = 'Projects'
    __table_args__ = {'extend_existing': True}
    id = Column(Integer, primary_key=True)
    user_id = Column(Integer)
    time = Column(Integer)
    posts_id = Column(String)
    posts_tags = Column(String)
    name = Column(String)

class VKPost(VKBase):
    __tablename__ = 'Posts'
    __table_args__ = {'extend_existing': True}
    id = Column(Integer, primary_key=True)
    post_url = Column(String)
    post_text = Column(String)
    post_id = Column(Integer)
    post_owner_id = Column(Integer)
    post_like = Column(Integer)
    post_repost = Column(Integer)
    post_view = Column(Integer)
    post_tags = Column(String)
    post_time = Column(Integer)