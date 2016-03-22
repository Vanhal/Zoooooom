package tv.vanhal.zoooooom.blocks;

import java.util.ArrayList;
import java.util.List;

import cofh.api.energy.IEnergyHandler;
import akka.util.Collections;
import tv.vanhal.zoooooom.Zoooooom;
import tv.vanhal.zoooooom.blockstates.PropertyConnection;
import tv.vanhal.zoooooom.enums.EnumConnection;
import tv.vanhal.zoooooom.enums.EnumPipe;
import tv.vanhal.zoooooom.enums.EnumType;
import tv.vanhal.zoooooom.items.ItemPipe;
import tv.vanhal.zoooooom.tiles.TilePipeExtract;
import tv.vanhal.zoooooom.tiles.TilePipeState;
import tv.vanhal.zoooooom.util.Colours;
import tv.vanhal.zoooooom.util.PipeState;
import tv.vanhal.zoooooom.util.Ref;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BasePipe extends BlockContainer  {
	public static final ArrayList<PropertyConnection> connections = new ArrayList<PropertyConnection>();
	
	public String name;
	public EnumPipe type;

	public BasePipe(String _name, EnumPipe _type) {
		super(Material.iron);
		this.name = _name;
		this.type = _type;

		setUnlocalizedName(_name);

		setHardness(1.0f);
		setCreativeTab(Zoooooom.ZTab);
        //setBlockBounds(0.35f, 0.35f, 0.35f, 0.65f, 0.65f, 0.65f);
	}
	
	//state stuff
	@Override
    protected BlockState createBlockState() {
		checkProperties();
		return new BlockState(this, new IProperty[] {
				connections.get(0), connections.get(1), connections.get(2),	
				connections.get(3), connections.get(4), connections.get(5)
			});
    }
	
	protected void setDefaultState() {
		checkProperties();
		setDefaultState(blockState.getBaseState().withProperty(connections.get(0), EnumConnection.none)
				.withProperty(connections.get(1), EnumConnection.none)
				.withProperty(connections.get(2), EnumConnection.none)
				.withProperty(connections.get(3), EnumConnection.none)
				.withProperty(connections.get(4), EnumConnection.none)
				.withProperty(connections.get(5), EnumConnection.none));
	}
	
	protected void checkProperties() {
		if (connections.size()==0) {
			for (int i = 0; i < EnumFacing.values().length; i++) {
				connections.add(i, PropertyConnection.create(EnumFacing.getFront(i).toString()));
			}
		}
	}
	
	@Override
    public int getMetaFromState(IBlockState state) {
		return 0;
    }
	
	@Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return updateConnections(worldIn, pos, state);
    }
	
	//rendering stuff
	@Override
    public boolean isFullBlock() {
        return false;
	}
	
	
	@Override
    public boolean isOpaqueCube() { 
    	return false; 
    }
	
	@Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean isVisuallyOpaque() {
        return false;
    }
    
    public int getRenderType() {
        return 3;
    }
	
	//deal with the bounds
	@Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
		float minX = 0.35f, minY = 0.35f, minZ = 0.35f;
		float maxX = 0.65f, maxY = 0.65f, maxZ = 0.65f;
		
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TilePipeState) {
			PipeState state = ((TilePipeState)tile).getState();
			if (state.isConnected(EnumFacing.UP)) maxY = 1.0f;
			if (state.isConnected(EnumFacing.DOWN)) minY = 0.0f;
			if (state.isConnected(EnumFacing.SOUTH)) maxZ = 1.0f;
			if (state.isConnected(EnumFacing.NORTH)) minZ = 0.0f;
			if (state.isConnected(EnumFacing.WEST)) minX = 0.0f;
			if (state.isConnected(EnumFacing.EAST)) maxX = 1.0f;
		}
		
        setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
    }
	
	@Override
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, 
    		List<AxisAlignedBB> list, Entity collidingEntity) {
    	setBlockBoundsBasedOnState(worldIn, pos);
    	super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
    }

	
    
    //init stuff
	public void preInit() {
		GameRegistry.registerBlock(this, ItemPipe.class, name);
	}
	
	public void init() {
		addRecipe();
	}
	
	protected void addRecipe() {
		Item core = Item.getItemFromBlock(Blocks.redstone_block);
		if (type.type == EnumType.Fluid) core = Items.bucket;
		if (type.type == EnumType.Item) core = Item.getItemFromBlock(Blocks.chest);
		ShapedOreRecipe recipe = new ShapedOreRecipe(new ItemStack(this, 8), new Object[]{
			"igi", "gcg", "igi", 'g', Blocks.glass, 'i', type.craftItem, 'c', core});
		GameRegistry.addRecipe(recipe);
	}

	public void postInit() {
		if (Zoooooom.proxy.isClient()) {
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
				.register(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":" + name, "inventory"));
		}
	}
	
	//block update stuff
	@Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		if (!worldIn.isRemote) {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof TilePipeState) {
				((TilePipeState)tile).updateState();
			}
			worldIn.markBlockForUpdate(pos);
		}
    }
	
	@Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		if (!worldIn.isRemote) {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof TilePipeState) {
				((TilePipeState)tile).updateState();
			}
			worldIn.markBlockForUpdate(pos);
		}
    }
	
	protected IBlockState updateConnections(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
		TileEntity tile = worldIn.getTileEntity(pos);
		if (tile instanceof TilePipeState) {
			TilePipeState pipeTile = (TilePipeState) tile;
			for (EnumFacing face: EnumFacing.values()) {
				state = state.withProperty(connections.get(face.ordinal()), pipeTile.getState().getState(face));
			}
		}
		return state;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TilePipeState(type);
	}
	
	//block clicky
	@Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (playerIn.getCurrentEquippedItem()==null) {
			TileEntity tile = world.getTileEntity(pos);
			if (tile instanceof TilePipeState) {
				TilePipeState pipeState = (TilePipeState)tile;
				return pipeState.changeConnection(side, hitX, hitY, hitZ);
			}
		}
		return false;
    }
}
