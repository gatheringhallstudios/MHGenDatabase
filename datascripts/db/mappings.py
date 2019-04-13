from sqlalchemy import Column, Integer, Float, Text, Boolean
from sqlalchemy import ForeignKey, UniqueConstraint
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import composite, relationship

Base = declarative_base()

class Monster(Base):
    __tablename__ = 'monsters'

    _id = Column(Integer, primary_key=True)
    _class = Column('class', Text)
    name = Column(Text)

class Item(Base):
    __tablename__ = 'items'

    _id = Column(Integer, primary_key=True)
    name = Column(Text)

    rarity = Column(Integer)
    type = Column(Text)

    components = relationship("Component", primaryjoin="Item._id == Component.created_item_id")

class Component(Base):
    __tablename__ = 'components'
    
    _id = Column(Integer, primary_key=True)
    created_item_id = Column(Integer, ForeignKey('items._id'))
    component_item_id = Column(Integer, ForeignKey('items._id'))
    key = Column(Boolean)

    created_item = relationship("Item", foreign_keys=[created_item_id])
    component_item = relationship("Item", foreign_keys=[component_item_id])

class HuntingReward(Base):
    __tablename__ = 'hunting_rewards'

    _id = Column(Integer, primary_key=True)
    item_id = Column(Integer, ForeignKey('items._id'))
    monster_id = Column(Integer, ForeignKey('monsters._id'))

    monster = relationship("Monster")

class Gathering(Base):
    __tablename__ = 'gathering'
    _id = Column(Integer, primary_key=True)
    item_id = Column(Integer, ForeignKey('items._id'))

    item = relationship("Item")

class Quest(Base):
    __tablename__ = 'quests'
    _id = Column(Integer, primary_key=True)
    name = Column(Text)

class MonsterQuest(Base):
    __tablename__ = 'monster_to_quest'
    _id = Column(Integer, primary_key=True)
    monster_id = Column(Integer, ForeignKey('monsters._id'))
    quest_id = Column(Integer, ForeignKey('quests._id'))
    unstable = Column(Boolean)

class QuestReward(Base):
    __tablename__ = 'quest_rewards'
    _id = Column(Integer, primary_key=True)
    quest_id = Column(Integer, ForeignKey('items._id'))
    item_id = Column(Integer, ForeignKey('items._id'))

class ArmorFamily(Base):
    __tablename__ = 'armor_families'
    _id = Column(Integer, primary_key=True)
    name = Column(Text)
    armor_pieces = relationship("Armor")

class Armor(Base):
    __tablename__ = 'armor'
    _id = Column(Integer, ForeignKey("items._id"), primary_key=True)
    family_id = Column("family", Integer, ForeignKey("armor_families._id"))

    item = relationship("Item", lazy="joined")
